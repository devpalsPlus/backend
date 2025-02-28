package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.LoginRequest;
import hs.kr.backend.devpals.domain.auth.dto.LoginResponse;
import hs.kr.backend.devpals.domain.auth.dto.TokenResponse;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseCookie;
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
    public ResponseEntity<LoginResponse<TokenResponse>> login(LoginRequest request) {
        // 이메일로 유저 조회
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorException.INVALID_PASSWORD);
        }

        // AccessToken, RefreshToken 생성
        String accessToken = jwtTokenProvider.generateToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // RefreshToken을 DB에 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // RefreshToken을 HttpOnly Secure 쿠키에 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(14 * 24 * 60 * 60) // 14일
                .build();

        LoginUserResponse userDto = LoginUserResponse.fromEntity(user);
        TokenResponse tokenData = new TokenResponse(accessToken);

        LoginResponse<TokenResponse> finalResponse = new LoginResponse<>(
                true,
                "로그인 되었습니다.",
                tokenData,
                userDto
        );

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(finalResponse);
    }
}
