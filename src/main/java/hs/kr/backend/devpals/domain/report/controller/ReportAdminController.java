package hs.kr.backend.devpals.domain.report.controller;

import hs.kr.backend.devpals.domain.report.dto.ReportDetailResponse;
import hs.kr.backend.devpals.domain.report.dto.ReportSummaryResponse;
import hs.kr.backend.devpals.domain.report.service.ReportAdminService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Report Admin API", description = "신고 조회관련 관리자 기능")
public class ReportAdminController {

    private final ReportAdminService reportAdminService;

    @GetMapping("/report")
    @Operation(summary = "신고 전체 조회", description = "전체 신고 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신고 목록 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "신고 목록 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"신고 목록을 조회하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<ReportSummaryResponse>>> getAllReports(@RequestHeader("Authorization") String token) {
        return reportAdminService.getAllReports(token);
    }

    @GetMapping("/report/{reportId}")
    @Operation(summary = "신고 상세 조회", description = "특정 신고의 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신고 상세 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "신고 상세 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"신고 상세 정보를 조회하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ReportDetailResponse>> getReportDetail(
            @PathVariable Long reportId,
            @RequestHeader("Authorization") String token) {
        return reportAdminService.getReportDetail(reportId, token);
    }

    @PatchMapping("/report/{reportId}/impose")
    @Operation(summary = "신고에 대해 경고 부여", description = "관리자가 특정 신고에 대해 경고를 부여합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "경고 부여 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "경고 부여 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"이미 제재된 신고입니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<Void>> imposeWarning(
            @PathVariable Long reportId,
            @RequestHeader("Authorization") String token
    ) {
        return reportAdminService.imposeWarning(reportId, token);
    }

}

