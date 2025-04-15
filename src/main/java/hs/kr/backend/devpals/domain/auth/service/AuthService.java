package hs.kr.backend.devpals.domain.auth.service;

import hs.kr.backend.devpals.domain.auth.dto.LoginRequest;
import hs.kr.backend.devpals.domain.auth.dto.LoginResponse;
import hs.kr.backend.devpals.domain.auth.dto.SignUpRequest;
import hs.kr.backend.devpals.domain.auth.dto.TokenResponse;
import hs.kr.backend.devpals.domain.auth.repository.AuthenticodeRepository;
import hs.kr.backend.devpals.domain.auth.util.CookieUtil;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenProvider;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final AuthenticodeRepository authenticodeRepository;

    // 로그인
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

    // 로그아웃
    public ResponseEntity<ApiResponse<String>> logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }

        try {
            jwtTokenValidator.invalidateToken(token); // AccessToken 무효화

            // RefreshToken 삭제 (DB에서 지우기)
            Long userId = jwtTokenValidator.getUserId(token);
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
            user.updateRefreshToken(null); // RefreshToken 제거
            userRepository.save(user);

            ApiResponse<String> response = new ApiResponse<>(true, "로그아웃 되었습니다", null);

            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorException.SERVER_ERROR);
        }
    }

    // 회원가입
    public ResponseEntity<ApiResponse<LoginUserResponse>> signUp(SignUpRequest request) {
        String email = request.getEmail();
        String nickname = request.getNickname();
        String password = request.getPassword();
        Boolean beginner = request.getBeginner();

        // 이메일 인증 여부 확인
        authenticodeRepository.findTopByUserEmailOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new CustomException(ErrorException.EMAIL_VERTIFICATION));


        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorException.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorException.DUPLICATE_NICKNAME);
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(password);

        // 유저 저장
        UserEntity user = new UserEntity(email, hashedPassword, nickname, beginner);
        userRepository.save(user);

        LoginUserResponse userDto = LoginUserResponse.fromEntity(user);

        ApiResponse<LoginUserResponse> response = new ApiResponse<>(true,"회원가입이 완료되었습니다.", userDto);

        return ResponseEntity.ok(response);
    }

    // Token Refresh
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
