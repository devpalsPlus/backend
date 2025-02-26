package hs.kr.backend.devpals.domain.auth.controller;

import hs.kr.backend.devpals.domain.auth.dto.*;
import hs.kr.backend.devpals.domain.auth.service.*;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final SignUpService signUpService;
    private final TokenRefreshService tokenRefreshService;
    private final EmailService emailService;

    // 회원가입 API
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<LoginUserResponse>> signUp(@RequestBody SignUpRequest request) {
        return signUpService.signUp(request);
    }
    //  로그인 API (JWT 반환)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDataResponse>> login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }

    // Refresh Token API
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDataResponse>> tokenRefresh(@RequestBody TokenRefreshRequest request) {
        return tokenRefreshService.tokenRefreshRequest(request);
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> emailSend(@RequestBody EmailRequest request){
        return emailService.emailSend(request);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> emailVerify(@RequestBody EmailVertificationRequest request){
        return emailService.sendEmailVerification(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token){
        return logoutService.logout(token);
    }
}
