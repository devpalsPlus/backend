package hs.kr.backend.devpals.domain.auth.controller;

import hs.kr.backend.devpals.domain.auth.dto.LoginRequest;
import hs.kr.backend.devpals.domain.auth.dto.TokenRefreshRequest;
import hs.kr.backend.devpals.domain.auth.service.SignUpService;
import hs.kr.backend.devpals.domain.auth.service.LoginService;
import hs.kr.backend.devpals.domain.auth.service.TokenRefreshService;
import hs.kr.backend.devpals.domain.user.dto.SingUpRequest;
import hs.kr.backend.devpals.global.facade.FacadeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final LoginService loginService;
    private final SignUpService signUpService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody SingUpRequest request) {
        return signUpService.signUp(request);
    }
    //  로그인 API (JWT 반환)
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> tokenRefresh(@RequestBody TokenRefreshRequest request) {
        return tokenRefreshService.tokenRefreshRequest(request);
    }
}
