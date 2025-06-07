package hs.kr.backend.devpals.domain.Inquiry.controller;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryPreviewResponse;
import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
import hs.kr.backend.devpals.domain.Inquiry.service.InquiryAdminService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "답변 등록 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 이미 답변됨"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 문의 없음")
            }
    )
    public ResponseEntity<ApiResponse<String>> registerAnswer(
            @RequestHeader("Authorization") String token,
            @PathVariable Long inquiryId,
            @RequestParam String answer
    ) {
        return inquiryAdminService.registerAnswer(token, inquiryId, answer);
    }

    @PatchMapping("/{inquiryId}/answer")
    @Operation(
            summary = "문의 답변 수정",
            description = "이미 등록된 문의 답변을 수정합니다. (관리자 전용)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "답변 수정 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "답변이 존재하지 않음"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 문의 없음")
            }
    )
    public ResponseEntity<ApiResponse<String>> updateAnswer(
            @RequestHeader("Authorization") String token,
            @PathVariable Long inquiryId,
            @RequestParam String answer
    ) {
        return inquiryAdminService.updateAnswer(token, inquiryId, answer);
    }

    @GetMapping
    @Operation(
            summary = "모든 문의글 조회",
            description = "등록된 모든 문의글 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모든 문의글 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 토큰 오류 등 발생")
            }
    )
    public ResponseEntity<ApiResponse<List<InquiryPreviewResponse>>> getAllInquiries(
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return inquiryAdminService.getAllInquiries(keyword);
    }

    @GetMapping("/{inquiryId}")
    @Operation(
            summary = "문의 상세 조회",
            description = "문의의 상세 내용을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 상세 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 토큰 오류 등 발생")
            }
    )
    public ResponseEntity<ApiResponse<InquiryResponse>> getInquiryDetail(@PathVariable Long inquiryId) {
        return inquiryAdminService.getInquiryDetail(inquiryId);
    }

}
