package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.TokenRefreshRequest;
import hs.kr.backend.devpals.domain.auth.entity.SessionEntity;
import hs.kr.backend.devpals.domain.auth.repository.SessionRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.facade.FacadeResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<Map<String, Object>> tokenRefreshRequest(TokenRefreshRequest request) {
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
        if (session.getExpiresAt().isBefore(LocalDateTime.now().plusDays(3))) { // 만료 3일 전이면 갱신
            newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
            session.setRefreshToken(newRefreshToken);
            session.setExpiresAt(LocalDateTime.now().plusDays(14)); // 14일 연장
        }

        // 세션 정보 업데이트
        session.setAccessToken(newAccessToken);
        sessionRepository.save(session);


        // 응답 데이터 구성
        Map<String, Object> tokenData = new LinkedHashMap<>();
        tokenData.put("accessToken", newAccessToken);
        tokenData.put("refreshToken", newRefreshToken);

        FacadeResponse<Map<String, Object>> facadeResponse = new FacadeResponse<>(true, "토큰 갱신 성공", tokenData);

        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("success", facadeResponse.isSuccess());
        finalResponse.put("message", facadeResponse.getMessage());
        finalResponse.put("data", facadeResponse.getData());

        return ResponseEntity.ok(finalResponse);
    }
}

