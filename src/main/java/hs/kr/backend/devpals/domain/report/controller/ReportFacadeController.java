package hs.kr.backend.devpals.domain.report.controller;

import hs.kr.backend.devpals.domain.report.dto.ReportDetailResponse;
import hs.kr.backend.devpals.domain.report.dto.ReportSummaryResponse;
import hs.kr.backend.devpals.domain.report.dto.ReportTagRequest;
import hs.kr.backend.devpals.domain.report.entity.ReportTagEntity;
import hs.kr.backend.devpals.domain.report.facade.ReportFacade;
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
@Tag(name = "Report API", description = "신고사유(카테고리)관련 API")
public class ReportFacadeController {
    private final ReportFacade reportFacade;

    @PostMapping("/report-tag")
    @Operation(summary = "신고사유(카테고리) 등록", description = "신고사유(카테고리)를 저장합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신고사유(카테고리) 저장 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "신고사유(카테고리) 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"신고사유(카테고리)를 저장하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ReportTagEntity>> createReportTag(@RequestBody ReportTagRequest request) {
        return reportFacade.createReportTag(request);
    }

    @GetMapping("/report-tag")
    @Operation(summary = "신고사유(카테고리) 조회", description = "저장된 모든 신고사유(카테고리)를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신고사유(카테고리) 목록 가져오기 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "신고사유 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"신고사유를 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<ReportTagEntity>>> getReportTag() {
        return reportFacade.getReportTags();
    }

    @DeleteMapping("/report-tag")
    @Operation(summary = "신고사유(카테고리) 삭제", description = "신고사유(카테고리)를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신고사유(카테고리) 삭제 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "신고사유(카테고리) 삭제 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"신고사유(카테고리)를 삭제하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> deleteReportTag(@RequestParam Long reportTagId) {
        return reportFacade.deleteReportTag(reportTagId);
    }

}
