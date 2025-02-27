package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.EmailRequest;
import hs.kr.backend.devpals.domain.auth.dto.EmailVertificationRequest;
import hs.kr.backend.devpals.domain.auth.entity.EmailVertificationEntity;
import hs.kr.backend.devpals.domain.auth.repository.AuthenticodeRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final AuthenticodeRepository authenticodeRepository;

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

        ApiResponse<String> apiResponse = new ApiResponse<>(true, "인증 코드가 이메일로 전송되었습니다.", null);

        return ResponseEntity.ok(apiResponse);
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

        ApiResponse<String> apiResponse = new ApiResponse<>(true, "이메일 인증 성공", null);

        return ResponseEntity.ok(apiResponse);
    }


    private void sendEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("Devpals <skaehgus113@naver.com>");
        message.setSubject("DevPals 이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode + "\n\n해당 코드를 입력하여 이메일 인증을 완료하세요.");

        javaMailSender.send(message);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6자리 랜덤 숫자 생성
    }

}
