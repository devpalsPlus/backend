package hs.kr.backend.devpals.domain.auth.controller;

import hs.kr.backend.devpals.domain.auth.dto.*;
import hs.kr.backend.devpals.domain.auth.service.EmailService;
import hs.kr.backend.devpals.domain.auth.service.SignUpService;
import hs.kr.backend.devpals.domain.auth.service.LoginService;
import hs.kr.backend.devpals.domain.auth.service.TokenRefreshService;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final LoginService loginService;
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
    public ResponseEntity<ApiResponse<LoginFinalResponse>> login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }

    // Refresh Token API
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDataResponse>> tokenRefresh(@RequestBody TokenRefreshRequest request) {
        return tokenRefreshService.tokenRefreshRequest(request);
    }

    @PostMapping("/email-send")
    public ResponseEntity<ApiResponse<String>> emailSend(@RequestBody EmailRequest request){
        return emailService.emailSend(request);
    }

    @PostMapping("/email-verify")
    public ResponseEntity<ApiResponse<String>> emailVerify(@RequestBody EmailVertificationRequest request){
        return emailService.sendEmailVerification(request);
    }
}
