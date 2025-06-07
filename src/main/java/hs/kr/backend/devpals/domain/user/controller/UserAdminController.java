package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.AdminUserListResponse;
import hs.kr.backend.devpals.domain.user.dto.AdminUserResponse;
import hs.kr.backend.devpals.domain.user.dto.UserAdminPreviewResponse;
import hs.kr.backend.devpals.domain.user.service.UserAdminService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "User Admin API", description = "관리자가 관리하는 유저 API")
public class UserAdminController {

    private UserAdminService userAdminService;

    @GetMapping("/preview")
    @Operation(summary = "회원 전체 미리보기", description = "관리자용 전체 회원 미리보기를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 미리보기 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "회원 미리보기 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"유저 정보를 조회할 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<UserAdminPreviewResponse>>> getAllUsersPreview(@RequestHeader("Authorization") String token) {
        return userAdminService.getAllUsersPreview(token);
    }

    @GetMapping
    @Operation(summary = "회원 전체 상세 조회", description = "관리자용 전체 회원 정보를 상세히 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유저 정보 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"유저 정보를 조회할 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<AdminUserListResponse>> getAllUsersDetail(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String keyword) {
        return userAdminService.getAllUsersDetail(page, size, keyword, token);
    }
}
