package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.LoginRequest;
import hs.kr.backend.devpals.domain.auth.dto.TokenDataResponse;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;

    @Transactional
    public ResponseEntity<ApiResponse<TokenDataResponse>> login(LoginRequest request) {
        // 이메일로 유저 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorException.INVALID_PASSWORD);
        }

        String existingRefreshToken = user.getRefreshToken();
        boolean isValidRefreshToken = existingRefreshToken != null && jwtTokenValidator.validateRefreshToken(existingRefreshToken);

        String refreshToken;
        if (isValidRefreshToken) {
            refreshToken = existingRefreshToken;
        } else {
            refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
            user.updateRefreshToken(refreshToken);
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user.getId());

        TokenDataResponse tokenData = new TokenDataResponse(accessToken, refreshToken);
        LoginUserResponse userDto = LoginUserResponse.fromEntity(user);

        ApiResponse<TokenDataResponse> finalResponse = new ApiResponse<>(
                true,
                "로그인 되었습니다.",
                tokenData,
                userDto
        );

        return ResponseEntity.ok(finalResponse);
    }
}
