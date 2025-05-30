package hs.kr.backend.devpals.domain.Inquiry.controller;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryDto;
import hs.kr.backend.devpals.domain.Inquiry.service.InquiryService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
@Tag(name = "Inquiry API", description = "문의 관련 API")
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "문의 작성",
            description = "사용자가 제목, 내용, 카테고리, 이미지 파일을 첨부하여 문의를 작성합니다. 이미지는 여러 장 업로드 가능합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 작성 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효하지 않은 파일 타입, 토큰 오류 등 발생")
            }
    )
    public ResponseEntity<ApiResponse<String>> createInquiry(
            @RequestHeader("Authorization") String token,
            @RequestPart("inquiryDto") InquiryDto inquiryDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return inquiryService.createInquiry(token, inquiryDto, images);
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
    public ResponseEntity<ApiResponse<List<InquiryDto>>> getAllInquiries() {
        return inquiryService.getAllInquiries();
    }


    @GetMapping("/{inquiryId}")
    @Operation(summary = "문의 조회", description = "특정 문의를 상세 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효하지 않은 파일 타입, 토큰 오류 등 발생")
            }
    )
    public ResponseEntity<ApiResponse<InquiryDto>> getInquiry(
            @PathVariable Long inquiryId) {
        return inquiryService.getInquiry(inquiryId);
    }

    @DeleteMapping("/{inquiryId}")
    @Operation(summary = "문의 삭제", description = "특정 문의를 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 삭제 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 토큰 오류 등 발생")
            }
    )
    public ResponseEntity<ApiResponse<String>> deleteInquiry(
            @RequestHeader("Authorization") String token,
            @PathVariable Long inquiryId) {
        return inquiryService.deleteInquiry(token, inquiryId);
    }

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
        return inquiryService.registerAnswer(token, inquiryId, answer);
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
        return inquiryService.updateAnswer(token, inquiryId, answer);
    }


}
