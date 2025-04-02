package hs.kr.backend.devpals.domain.auth.controller;

import hs.kr.backend.devpals.domain.auth.dto.*;
import hs.kr.backend.devpals.domain.auth.service.*;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.global.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;
    private final AuthEmailService emailService;

    // 회원가입 API
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<LoginUserResponse>> signUp(@RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }
    // 로그인 API (JWT 반환)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token){
        return authService.logout(token);
    }

    // Refresh Token API
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> tokenRefresh(HttpServletRequest request) {
        return authService.tokenRefreshRequest(request);
    }

    @PostMapping("/email-send")
    public ResponseEntity<ApiResponse<String>> emailSend(@RequestBody EmailRequest request){
        return emailService.emailSend(request);
    }

    @PostMapping("/email-verify")
    public ResponseEntity<ApiResponse<String>> emailVerify(@RequestBody EmailVertificationRequest request){
        return emailService.sendEmailVerification(request);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request){
        return emailService.resetPassword(request);
    }
}
