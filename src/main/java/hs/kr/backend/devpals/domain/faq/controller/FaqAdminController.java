package hs.kr.backend.devpals.domain.faq.controller;

import hs.kr.backend.devpals.domain.faq.dto.FaqDTO;
import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.faq.service.FaqService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
@Tag(name = "FAQ Admin API", description = "FAQ Admin API")
public class FaqAdminController {
    private final FaqAdminService faqAdminService;

    @PostMapping
    @Operation(
            summary = "FAQ 작성",
            description = "관리자가 새로운 FAQ를 작성합니다. 질문과 답변을 입력받습니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 작성 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효하지 않은 입력 등 발생")
            }
    )
    public ResponseEntity<ApiResponse<String>> createFaq(
            @RequestHeader("Authorization") String token,
            @RequestBody FaqDTO faqDTO) {
        return faqAdminService.createFaq(token, faqDTO);
    }

    @PutMapping("/{faqId}")
    @Operation(
            summary = "FAQ 수정",
            description = "기존 FAQ의 질문 및 답변 내용을 수정합니다. FAQ ID와 함께 수정할 내용을 전달해야 합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 수정 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 존재하지 않는 FAQ ID 등")
            }
    )
    public ResponseEntity<ApiResponse<String>> updateFaq(
            @RequestHeader("Authorization") String token,
            @PathVariable Long faqId,
            @RequestBody FaqDTO faqDTO) {
        return faqAdminService.updateFaq(token, faqId, faqDTO);
    }

    @DeleteMapping("/{faqId}")
    @Operation(
            summary = "FAQ 삭제",
            description = "FAQ ID를 기반으로 특정 FAQ를 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 삭제 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 존재하지 않는 ID 등")
            }
    )
    public ResponseEntity<ApiResponse<String>> deleteFaq(
            @RequestHeader("Authorization") String token,
            @PathVariable Long faqId){
        return faqAdminService.deleteFaq(token, faqId);
    }
}
