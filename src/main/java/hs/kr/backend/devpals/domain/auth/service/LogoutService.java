package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;

    public ResponseEntity<ApiCustomResponse<String>> logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }

        try {
            jwtTokenValidator.invalidateToken(token); // AccessToken 무효화

            //  RefreshToken 삭제 (DB에서 지우기)
            Long userId = jwtTokenValidator.getUserId(token);
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
            user.updateRefreshToken(null); // RefreshToken 제거
            userRepository.save(user);

            ApiCustomResponse<String> response = new ApiCustomResponse<>(true, "로그아웃 되었습니다", null);

            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorException.SERVER_ERROR);
        }
    }
}
