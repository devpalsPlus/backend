package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenValidator jwtTokenValidator;

    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }

        Integer userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.fromEntity(user);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "사용자의 정보입니다.", userResponse);

        return ResponseEntity.ok(response);
    }

}
