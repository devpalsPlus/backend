package hs.kr.backend.devpals.domain.auth.service;

import java.time.LocalDateTime;
import hs.kr.backend.devpals.domain.auth.dto.SignUpRequest;
import hs.kr.backend.devpals.domain.auth.entity.EmailVertificationEntity;
import hs.kr.backend.devpals.domain.auth.repository.AuthenticodeRepository;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final AuthenticodeRepository authenticodeRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(SignUpService.class);

    public ResponseEntity<ApiResponse<LoginUserResponse>> signUp(SignUpRequest request) {
        String email = request.getEmail();
        String nickname = request.getNickname();
        String password = request.getPassword();

        logger.info("회원가입 요청 이메일: {}", email);

        // 이메일 인증 여부 확인
        EmailVertificationEntity authCode = authenticodeRepository.findTopByUserEmailOrderByExpiresAtDesc(email)
                .orElseThrow(() -> {
                    logger.error("이메일 인증 코드가 존재하지 않습니다: {}", email);
                    return new CustomException(ErrorException.INVALID_CODE);
                });

        logger.info("조회된 인증 코드: {} (사용됨: {})", authCode.getCode(), authCode.isUsed());

        // 만료된 코드인지 확인
        if (authCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.error("인증 코드가 만료되었습니다: {}", authCode.getCode());
            throw new CustomException(ErrorException.TOKEN_EXPIRED);
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorException.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorException.DUPLICATE_NICKNAME);
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(password);

        // 유저 저장
        UserEntity user = new UserEntity(email, hashedPassword, nickname);
        userRepository.save(user);

        LoginUserResponse userDto = LoginUserResponse.fromEntity(user);

        ApiResponse<LoginUserResponse> response = new ApiResponse<>(
                true,
                "회원가입이 완료되었습니다.",
                userDto
        );

        return ResponseEntity.ok(response);
    }
}
