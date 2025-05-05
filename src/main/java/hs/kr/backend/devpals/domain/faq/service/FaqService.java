package hs.kr.backend.devpals.domain.faq.service;

import hs.kr.backend.devpals.domain.faq.dto.FaqDTO;
import hs.kr.backend.devpals.domain.faq.entity.FaqEntity;
import hs.kr.backend.devpals.domain.faq.repository.FaqRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    public ResponseEntity<ApiResponse<String>> createFaq(String token, FaqDTO request) {
        FaqEntity faq = FaqEntity.from(request);

        faqRepository.save(faq);

        return ResponseEntity.ok(new ApiResponse<>(true, "FAQ 작성 성공", null));
    }

    public ResponseEntity<ApiResponse<List<FaqDTO>>> getAllFaq() {
        List<FaqDTO> faqList = faqRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(FaqDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "FAQ 전체 조회 성공", faqList));
    }

    public ResponseEntity<ApiResponse<FaqDTO>> getFaq(String token, Long faqId) {
        FaqEntity faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new CustomException(ErrorException.FAQ_NOT_FOUND));

        FaqDTO responseDto = FaqDTO.fromEntity(faq);

        return ResponseEntity.ok(new ApiResponse<>(true, "FAQ 상세 조회 성공", responseDto));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateFaq(String token, Long id, FaqDTO faqDTO) {
        FaqEntity faq = faqRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorException.FAQ_NOT_FOUND));

        faq.update(faqDTO.getTitle(), faqDTO.getContent());
        faqRepository.save(faq);

        return ResponseEntity.ok(new ApiResponse<>(true, "FAQ 수정 완료", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteFaq(String token, Long faqId) {
        FaqEntity faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new CustomException(ErrorException.FAQ_NOT_FOUND));

        faqRepository.delete(faq);
        return ResponseEntity.ok(new ApiResponse<>(true, "FAQ 삭제 완료", null));
    }
}
