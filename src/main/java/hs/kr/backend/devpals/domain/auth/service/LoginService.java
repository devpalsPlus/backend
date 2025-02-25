package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.LoginFinalResponse;
import hs.kr.backend.devpals.domain.auth.dto.LoginRequest;
import hs.kr.backend.devpals.domain.auth.dto.TokenDataResponse;
import hs.kr.backend.devpals.domain.auth.entity.SessionEntity;
import hs.kr.backend.devpals.domain.auth.repository.SessionRepository;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.facade.FacadeResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRepository sessionRepository;

    @Transactional
    public ResponseEntity<FacadeResponse<LoginFinalResponse>> login(LoginRequest request) {
        // 이메일로 유저 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorException.INVALID_PASSWORD);
        }

        sessionRepository.deleteByUserId(user.getId());
        sessionRepository.flush();


        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 새로운 세션 저장
        SessionEntity session = SessionEntity.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build();

        sessionRepository.save(session);

        TokenDataResponse tokenData = new TokenDataResponse(accessToken, refreshToken);

        LoginUserResponse userDto = LoginUserResponse.fromEntity(user);

        LoginFinalResponse responseDto = new LoginFinalResponse(tokenData, userDto);

        FacadeResponse<LoginFinalResponse> finalResponse = new FacadeResponse<>(
                true,
                "로그인 되었습니다.",
                responseDto
        );

        return ResponseEntity.ok(finalResponse);
    }
}
