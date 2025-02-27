package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.TokenResponse;
import hs.kr.backend.devpals.domain.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse<TokenResponse>> tokenRefreshRequest(HttpServletRequest request) {
        // 쿠키에서 RefreshToken 가져오기
        String refreshToken = CookieUtil.getCookie(request, "refreshToken")
                .orElseThrow(() -> new CustomException(ErrorException.TOKEN_EXPIRED));

        // DB에서 RefreshToken 검증 (유저 조회)
        UserEntity user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // AccessToken이 아직 유효한지 확인
        String currentAccessToken = request.getHeader("Authorization"); // 요청 헤더에서 AccessToken 가져오기
        if (currentAccessToken != null && currentAccessToken.startsWith("Bearer ")) {
            currentAccessToken = currentAccessToken.substring(7);
            boolean isAccessTokenValid = jwtTokenValidator.validateJwtToken(currentAccessToken);

            if (isAccessTokenValid) {
                throw new CustomException(ErrorException.ACCESS_TOKEN_NOT_EXPIRED); // AccessToken이 유효하면 예외 발생
            }
        }

        // 새로운 AccessToken & RefreshToken 발급
        String newAccessToken = jwtTokenProvider.generateToken(user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // RefreshToken을 DB에 업데이트 (덮어쓰기)
        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);

        // 새로운 RefreshToken을 HttpOnly 쿠키에 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(14 * 24 * 60 * 60) // 14일 유지
                .build();

        TokenResponse tokenData = new TokenResponse(newAccessToken);

        ApiResponse<TokenResponse> response = new ApiResponse<>(true, "토큰 갱신 성공", tokenData);

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(response);
    }
}
