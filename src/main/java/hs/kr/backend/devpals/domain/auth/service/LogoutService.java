package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.facade.FacadeResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtTokenValidator jwtTokenValidator;

    public ResponseEntity<ApiResponse<String>> logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }

        try {
            jwtTokenValidator.invalidateToken(token); // 토큰 무효화 처리

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "로그아웃 되었습니다.",
                    null
            ));

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            throw new CustomException(ErrorException.SERVER_ERROR);
        }
    }
}
