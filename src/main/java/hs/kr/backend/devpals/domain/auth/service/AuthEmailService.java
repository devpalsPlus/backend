package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.EmailRequest;
import hs.kr.backend.devpals.domain.auth.dto.EmailVertificationRequest;
import hs.kr.backend.devpals.domain.auth.dto.ResetPasswordRequest;
import hs.kr.backend.devpals.domain.auth.entity.EmailVertificationEntity;
import hs.kr.backend.devpals.domain.auth.repository.AuthenticodeRepository;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthEmailService {

    private final JavaMailSender javaMailSender;
    private final AuthenticodeRepository authenticodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Qualifier("emailExecutor")
    private final Executor emailExecutor; // 병렬 실행을 위한 Executor 주입


    @Transactional
    public ResponseEntity<ApiResponse<String>> emailSend(EmailRequest request) {
        String email = request.getEmail();

        // 기존 인증 코드 삭제 (중복 방지)
        authenticodeRepository.deleteByUserEmail(email);

        // 새로운 인증 코드 생성
        String verificationCode = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5); // 5분 후 만료

        // DB에 저장
        EmailVertificationEntity authCode = new EmailVertificationEntity(email, verificationCode, expiresAt);
        authenticodeRepository.save(authCode);

        // 이메일 전송
        try {
            sendEmail(email, verificationCode);
        } catch (Exception e) {
            throw new CustomException(ErrorException.EMAIL_SEND_FAILED);
        }


        return ResponseEntity.ok(new ApiResponse<>(true, "인증 코드가 이메일로 전송되었습니다.", null));
    }

    public ResponseEntity<ApiResponse<String>> sendEmailVerification(EmailVertificationRequest request){
        String email = request.getEmail();
        String code = request.getCode();

        EmailVertificationEntity authCode = authenticodeRepository.findTopByUserEmailOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new CustomException(ErrorException.EMAIL_SEND_FAILED));

        // 만료 시간 확인
        if (authCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorException.EMAIL_CODE_EXPIRED);
        }

        // 코드 검증
        if (!authCode.getCode().equals(code)) {
            throw new CustomException(ErrorException.INVALID_CODE);
        }

        // 인증 완료 처리
        authCode.useCode();
        authenticodeRepository.save(authCode);

        return ResponseEntity.ok(new ApiResponse<>(true, "이메일 인증 성공", null));
    }

    /**
     * 지원자 리스트에 대해 비동기 이메일 전송
     */
    public CompletableFuture<Void> sendEmailsAsync(List<ApplicantEntity> applicants, ProjectEntity project) {
        int batchSize = 5; // 한 번에 5개씩 이메일 전송

        List<List<ApplicantEntity>> batches = new ArrayList<>();
        for (int i = 0; i < applicants.size(); i += batchSize) {
            batches.add(applicants.subList(i, Math.min(i + batchSize, applicants.size())));
        }

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> {
                    for (ApplicantEntity applicant : batch) {
                        sendEmailsToApplicantsByStatus(applicant, project);
                    }
                }, emailExecutor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> sendEmailsToApplicantsByStatus(ApplicantEntity applicant, ProjectEntity project) {
        return CompletableFuture.runAsync(() -> {
            String email = applicant.getEmail();

            String message = applicant.getStatus() == ApplicantStatus.ACCEPTED
                    ? "축하합니다! " + project.getTitle() + " 프로젝트에 합격하셨습니다."
                    : "안타깝지만 " + project.getTitle() + " 프로젝트에 불합격하셨습니다.";

            try {
                sendEmail(email, "DevPals 프로젝트 지원 결과", message);
            } catch (Exception e) {
                log.error("이메일 전송 중 오류 발생 - 프로젝트: {}, 지원자 이메일: {}, 오류 메시지: {}",
                        project.getTitle(), email, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, emailExecutor);
    }


    @Transactional
    public ResponseEntity<ApiResponse<String>> resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        String newPassword = request.getNewPassword();

        // 인증 코드 확인
        EmailVertificationEntity authCode = authenticodeRepository.findTopByUserEmailOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new CustomException(ErrorException.INVALID_CODE));

        // 입력된 코드가 저장된 코드와 일치하는지 확인
        if (!authCode.getCode().equals(code)) {
            throw new CustomException(ErrorException.INVALID_CODE);
        }

        // 인증 코드 만료 확인
        if (authCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorException.EMAIL_CODE_EXPIRED);
        }

        // 인증 성공 → 비밀번호 변경
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 비밀번호 변경 (암호화 필요)
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 인증 코드 사용 처리
        authCode.useCode();
        authenticodeRepository.save(authCode);

        return ResponseEntity.ok(new ApiResponse<>(true, "비밀번호가 성공적으로 변경되었습니다.", null));
    }



    private void sendEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("Devpals <skaehgus113@naver.com>");
        message.setSubject("DevPals 이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode + "\n\n해당 코드를 입력하여 이메일 인증을 완료하세요.");

        javaMailSender.send(message);
    }
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("Devpals <skaehgus113@naver.com>");
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6자리 랜덤 숫자 생성
    }

}
