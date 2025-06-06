package hs.kr.backend.devpals.domain.Inquiry.controller;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
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
            @RequestPart("inquiryDto") InquiryResponse inquiryResponse,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return inquiryService.createInquiry(token, inquiryResponse, images);
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

}
