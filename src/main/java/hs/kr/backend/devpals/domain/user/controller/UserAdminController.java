package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.*;
import hs.kr.backend.devpals.domain.user.service.UserAdminService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "관리자용 회원 프로젝트 정보 통합 조회",
            description = "관리자가 특정 회원의 프로젝트 작성, 참여, 지원 이력 등을 모두 조회합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "조회할 유저 ID", required = true),
                    @Parameter(name = "Authorization", description = "관리자 인증 토큰", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원의 프로젝트 통합 정보 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "관리자 권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"관리자 권한이 필요합니다.\", \"data\": null}")
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "유저를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null}")
                            )
                    )
            }
    )
    @GetMapping("/{userId}/projects")
    public ResponseEntity<ApiResponse<AdminUserProjectOverviewResponse>> adminGetUserProjects(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId
    ) {
        return userAdminService.adminGetProjectOverview(token, userId);
    }

    @Operation(
            summary = "관리자용 회원 활동 정보 통합 조회",
            description = "관리자가 특정 회원의 문의글과 댓글을 모두 조회합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "조회할 유저 ID", required = true),
                    @Parameter(name = "Authorization", description = "관리자 인증 토큰", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 활동 정보 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "관리자 권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"관리자 권한이 필요합니다.\", \"data\": null}")
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "유저를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null}")
                            )
                    )
            }
    )
    @GetMapping("/{userId}/activities")
    public ResponseEntity<ApiResponse<AdminUserActivityOverviewResponse>> adminGetUserActivities(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId
    ) {
        return userAdminService.adminGetUserActivity(token, userId);
    }

    @Operation(
            summary = "관리자용 방문 통계 조회",
            description = "관리자가 최근 30일 간의 웹 페이지 접속 횟수를 일별, 주별, 월별 단위로 확인할 수 있습니다.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "관리자 인증용 Bearer 토큰 (예: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...)",
                            required = true,
                            example = "Bearer <your_access_token>"
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "접속 통계 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = """
                {
                  "success": true,
                  "message": "방문 통계 조회 성공",
                  "data": {
                    "dailyVisits": {
                      "2025-07-01": 23,
                      "2025-07-02": 17
                    },
                    "weeklyVisits": {
                      "2025-W26": 108,
                      "2025-W27": 132
                    },
                    "monthlyVisits": {
                      "2025-06": 502,
                      "2025-07": 121
                    }
                  }
                }
                """)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "관리자 권한 없음 또는 토큰 누락",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"관리자 권한이 필요합니다.\", \"data\": null}")
                            )
                    )
            }
    )
    @GetMapping("/visits/stats")
    public ResponseEntity<ApiResponse<AdminVisitStatsResponse>> getVisitStats(
            @RequestHeader("Authorization") String token
    ) {
        return userAdminService.getVisitStats(token);
    }
}
