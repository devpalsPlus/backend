package hs.kr.backend.devpals.domain.faq.controller;

import hs.kr.backend.devpals.domain.faq.dto.FaqDTO;
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
@Tag(name = "FAQ API", description = "자주 묻는 질문 관련 API")
public class FaqController {
    private final FaqService faqservice;

    @GetMapping
    @Operation(
            summary = "전체 FAQ 조회",
            description = "등록된 모든 FAQ를 리스트로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전체 FAQ 조회 성공")
            }
    )
    public ResponseEntity<ApiResponse<List<FaqDTO>>> getAllFaqs(
            @RequestParam(defaultValue = "") String keyword) {
        return faqservice.getAllFaq(keyword);
    }


    @GetMapping("/{faqId}")
    @Operation(
            summary = "FAQ 상세 조회",
            description = "FAQ ID를 이용해 특정 FAQ의 질문과 답변을 상세 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FAQ 상세 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 존재하지 않는 ID 등")
            }
    )
    public ResponseEntity<ApiResponse<FaqDTO>> getFaq(
            @PathVariable Long faqId) {
        return faqservice.getFaq(faqId);
    }

}
