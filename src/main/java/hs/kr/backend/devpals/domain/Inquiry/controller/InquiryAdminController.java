package hs.kr.backend.devpals.domain.Inquiry.controller;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryAnswerRequest;
import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryPreviewResponse;
import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
import hs.kr.backend.devpals.domain.Inquiry.service.InquiryAdminService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
@Tag(name = "Inquiry Admin API", description = "문의 관련 관리자 API")
public class InquiryAdminController {
    private final InquiryAdminService inquiryAdminService;

    @PostMapping("/{inquiryId}/answer")
    @Operation(
            summary = "문의 답변 등록",
            description = "특정 문의에 대해 관리자가 최초 답변을 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "답변 등록 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": true, \"message\": \"답변 등록 성공\", \"data\": null}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "잘못된 요청 - 이미 답변됨",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"이미 답변이 등록된 문의입니다.\", \"data\": null}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404", description = "해당 문의 없음",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 문의를 찾을 수 없습니다.\", \"data\": null}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<String>> registerAnswer(
            @RequestHeader("Authorization") String token,
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest answer
    ) {
        return inquiryAdminService.registerAnswer(token, inquiryId, answer);
    }

    @PatchMapping("/{inquiryId}/answer")
    @Operation(
            summary = "문의 답변 수정",
            description = "이미 등록된 문의 답변을 수정합니다. (관리자 전용)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "답변 수정 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": true, \"message\": \"답변 수정 성공\", \"data\": null}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "답변이 존재하지 않음",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"답변이 존재하지 않아 수정할 수 없습니다.\", \"data\": null}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404", description = "해당 문의 없음",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 문의를 찾을 수 없습니다.\", \"data\": null}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<String>> updateAnswer(
            @RequestHeader("Authorization") String token,
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest answer
    ) {
        return inquiryAdminService.updateAnswer(token, inquiryId, answer);
    }

    @GetMapping
    @Operation(
            summary = "모든 문의글 조회",
            description = "유저 ID와 기간으로 필터링된 문의글 목록을 최신순으로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "문의글 조회 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": true, \"message\": \"문의글 조회 성공\", \"data\": [...]}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"요청 파라미터가 잘못되었습니다.\", \"data\": null}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<List<InquiryPreviewResponse>>> getAllInquiries(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return inquiryAdminService.getAllInquiries(userId, startDate, endDate);
    }

    @GetMapping("/{inquiryId}")
    @Operation(
            summary = "문의 상세 조회",
            description = "문의의 상세 내용을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "문의 상세 조회 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": true, \"message\": \"문의 상세 조회 성공\", \"data\": {...}}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "잘못된 요청 - 토큰 오류 등 발생",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"잘못된 요청입니다.\", \"data\": null}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<InquiryResponse>> getInquiryDetail(@PathVariable Long inquiryId) {
        return inquiryAdminService.getInquiryDetail(inquiryId);
    }

    @GetMapping("/preview")
    @Operation(
            summary = "미리보기용 문의글 10개 조회",
            description = "최근 문의글을 10개 미리보기 형태로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "문의 미리보기 조회 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": true, \"message\": \"미리보기 조회 성공\", \"data\": [...]}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "잘못된 요청 - 토큰 오류 등 발생",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"잘못된 요청입니다.\", \"data\": null}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<List<InquiryPreviewResponse>>> getInquiryPreviewList() {
        return inquiryAdminService.getInquiryPreviews();
    }
}
