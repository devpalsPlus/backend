package hs.kr.backend.devpals.domain.report.controller;


import hs.kr.backend.devpals.domain.report.service.ReportService;
import hs.kr.backend.devpals.domain.report.dto.ReportRequest;
import hs.kr.backend.devpals.domain.report.dto.ReportResponse;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "Report API", description = "신고 관련 API")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "신고 작성", description = "신고를 작성합니다. reportFilter의 값은 다음과 같습니다. USER(1),PROJECT(2),COMMENT(3),RECOMMENT(4)\n")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신고 작성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "신고 작성 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"신고 작성에 실패했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @RequestBody ReportRequest request,
            @RequestHeader("Authorization") String token) {
        return reportService.report(request, token);
    }

}
