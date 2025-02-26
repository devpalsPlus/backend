package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.TokenDataResponse;
import hs.kr.backend.devpals.domain.auth.dto.TokenRefreshRequest;
import hs.kr.backend.devpals.domain.auth.entity.SessionEntity;
import hs.kr.backend.devpals.domain.auth.repository.SessionRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse<TokenDataResponse>> tokenRefreshRequest(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // DB에서 Refresh Token 조회
        SessionEntity session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorException.UNAUTHORIZED));

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // Refresh Token 검증
        if (!jwtTokenValidator.validateRefreshToken(refreshToken)) {
            sessionRepository.delete(session); // 만료된 Refresh Token 제거
            throw new CustomException(ErrorException.TOKEN_EXPIRED);
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateToken(user.getId());

        // Refresh Token 갱신이 필요한 경우 새로 발급
        String newRefreshToken = refreshToken;
        LocalDateTime newExpiresAt = session.getExpiresAt();
        if (session.getExpiresAt().isBefore(LocalDateTime.now().plusDays(3))) { // 만료 3일 전이면 갱신
            newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
            newExpiresAt = LocalDateTime.now().plusDays(14);
        }

        // 세션 정보 업데이트 (기존 객체를 수정하지 않고 새로운 객체 생성)
        SessionEntity updatedSession = session.updateTokens(newAccessToken, newRefreshToken, newExpiresAt);
        sessionRepository.save(updatedSession);

        TokenDataResponse responseDto = new TokenDataResponse(newAccessToken, newRefreshToken);

        ApiResponse<TokenDataResponse> finalResponse = new ApiResponse<>(
                true,
                "토큰 갱신 성공",
                responseDto
        );

        return ResponseEntity.ok(finalResponse);
    }
}

