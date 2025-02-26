package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.TokenDataResponse;
import hs.kr.backend.devpals.domain.auth.dto.TokenRefreshRequest;
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
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ResponseEntity<FacadeResponse<TokenDataResponse>> tokenRefreshRequest(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 유효성 검사: 리프레시 토큰이 만료되었거나 올바르지 않다면 예외 발생
        if (!jwtTokenValidator.validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorException.TOKEN_EXPIRED);
        }

        // 리프레시 토큰에서 사용자 ID 추출
        Integer userId = jwtTokenValidator.getUserIdFromToken(refreshToken);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 사용자의 기존 리프레시 토큰과 요청된 토큰이 일치하는지 확인
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new CustomException(ErrorException.UNAUTHORIZED); // 다른 기기에서 로그인한 경우
        }

        //  새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateToken(userId);

        //  기존 리프레시 토큰이 유효하면 변경하지 않음
        boolean isValidRefreshToken = jwtTokenValidator.validateRefreshToken(user.getRefreshToken());
        String newRefreshToken = isValidRefreshToken ? user.getRefreshToken() : jwtTokenProvider.generateRefreshToken(userId);

        //  리프레시 토큰이 변경되었다면 UserEntity 업데이트
        if (!isValidRefreshToken) {
            user.updateRefreshToken(newRefreshToken);
            userRepository.save(user);
        }

        TokenDataResponse responseDto = new TokenDataResponse(newAccessToken, newRefreshToken);

        return ResponseEntity.ok(new FacadeResponse<>(
                true,
                "토큰 갱신 성공",
                responseDto
        ));
    }
}

