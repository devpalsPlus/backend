package hs.kr.backend.devpals.domain.auth.controller;

import hs.kr.backend.devpals.domain.auth.dto.*;
import hs.kr.backend.devpals.domain.auth.service.*;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.principal.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "회원가입 및 로그인 관련 API")
public class AuthController {

    private final AuthService authService;
    private final AuthEmailService authEmailService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "이메일을 통한 회원가입을 진행합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "이메일 중복, 닉네임 중복 또는 이메일 인증 미완료",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"이메일이 중복되었습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<LoginUserResponse>> signUp(@RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 입력하여 로그인을 진행합니다. 성공 시 JWT 토큰을 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "계정이 존재하지 않거나 비밀번호 오류",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"계정이 존재하지 않거나 비밀번호가 올바르지 않습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<LoginResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃하고, JWT 토큰을 만료 처리합니다. (클라이언트에서 AccessToken 삭제 과정 필요)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 토큰 또는 만료된 토큰",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"유효하지 않거나 만료된 토큰입니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 갱신 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Refresh Token이 만료됨",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Refresh Token이 만료되었습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<TokenResponse>> tokenRefresh(HttpServletRequest request) {
        return authService.tokenRefreshRequest(request);
    }

    @PostMapping("/email-send")
    @Operation(summary = "이메일 인증 요청", description = "입력한 이메일로 인증 코드를 전송합니다. (현재 네이버만 가능)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증 코드 전송 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "이메일 형식 오류 또는 전송 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"이메일 형식이 올바르지 않거나 전송에 실패했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> emailSend(@RequestBody EmailRequest request) {
        return authEmailService.emailSend(request);
    }

    @PostMapping("/email-verify")
    @Operation(summary = "이메일 인증 확인", description = "사용자가 입력한 인증 코드가 유효한지 확인합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "인증 코드 불일치 또는 만료됨",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"인증 코드가 일치하지 않거나 만료되었습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> emailVerify(@RequestBody EmailVertificationRequest request) {
        return authEmailService.sendEmailVerification(request);
    }

    @PostMapping("/password/reset")
    @Operation(summary = "비밀번호 재설정", description = "이메일을 통해 비밀번호를 재설정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "이메일 인증 실패 또는 코드 만료",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"이메일 인증에 실패했거나 코드가 만료되었습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        return authEmailService.resetPassword(request);
    }

    @GetMapping("/oauth-login")
    @Operation(summary = "Oauth2.0 정보 가져오기", description = "Oauth2.0 소셜 로그인 성공 시 정보를 가져옵니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oauth2.0 정보 가져오기 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "계정이 존재하지 않거나 비밀번호 오류",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"계정이 존재하지 않거나 비밀번호가 올바르지 않습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<LoginResponse<TokenResponse>> oauthLogin(
            @RequestHeader("Authorization") String token) {

        return authService.oauthLogin(token);
    }
}
