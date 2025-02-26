package hs.kr.backend.devpals.domain.auth.controller;

import hs.kr.backend.devpals.domain.auth.dto.*;
import hs.kr.backend.devpals.domain.auth.service.*;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.global.facade.FacadeResponse;
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
    public ResponseEntity<FacadeResponse<LoginUserResponse>> signUp(@RequestBody SignUpRequest request) {
        return signUpService.signUp(request);
    }
    //  로그인 API (JWT 반환)
    @PostMapping("/login")
    public ResponseEntity<FacadeResponse<TokenDataResponse>> login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }

    // Refresh Token API
    @PostMapping("/refresh")
    public ResponseEntity<FacadeResponse<TokenDataResponse>> tokenRefresh(@RequestBody TokenRefreshRequest request) {
        return tokenRefreshService.tokenRefreshRequest(request);
    }

    @PostMapping("/send")
    public ResponseEntity<FacadeResponse<String>> emailSend(@RequestBody EmailRequest request){
        return emailService.emailSend(request);
    }

    @PostMapping("/verify")
    public ResponseEntity<FacadeResponse<String>> emailVerify(@RequestBody EmailVertificationRequest request){
        return emailService.sendEmailVerification(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<FacadeResponse<String>> logout(@RequestHeader("Authorization") String token){
        return logoutService.logout(token);
    }
}
