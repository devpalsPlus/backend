package hs.kr.backend.devpals.domain.faq.service;

import hs.kr.backend.devpals.domain.faq.dto.FaqDTO;
import hs.kr.backend.devpals.domain.faq.entity.FaqEntity;
import hs.kr.backend.devpals.domain.faq.repository.FaqRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
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
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse<String>> createFaq(String token, FaqDTO request) {
        validateAdmin(token);

        FaqEntity faq = FaqEntity.from(request);

        faqRepository.save(faq);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "FAQ 작성 성공", null));
    }

    public ResponseEntity<ApiResponse<List<FaqDTO>>> getAllFaq(String keyword) {
        List<FaqEntity> faqs = keyword.isBlank()
                ? faqRepository.findAllByOrderByCreatedAtDesc()
                : faqRepository.findByTitleContainingOrderByCreatedAtDesc(keyword);

        List<FaqDTO> result = faqs.stream()
                .map(FaqDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(200, true, "FAQ 조회 성공", result));
    }


    public ResponseEntity<ApiResponse<FaqDTO>> getFaq(String token, Long faqId) {
        validateAdmin(token);

        return faqRepository.findById(faqId)
                .map(faq -> {
                    FaqDTO responseDto = FaqDTO.fromEntity(faq);
                    return ResponseEntity.ok(new ApiResponse<>(200, true, "FAQ 상세 조회 성공", responseDto));
                })
                .orElseGet(() ->
                        ResponseEntity.ok(new ApiResponse<>(404, false, "FAQ글이 존재하지 않습니다", null))
                );
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateFaq(String token, Long id, FaqDTO faqDTO) {
        validateAdmin(token);

        FaqEntity faq = faqRepository.findById(id).orElse(null);
        if (faq == null) {
            return ResponseEntity.ok(new ApiResponse<>(404, false, "FAQ글이 존재하지 않습니다", null));
        }

        faq.update(faqDTO.getTitle(), faqDTO.getContent());
        faqRepository.save(faq);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "FAQ 수정 완료", null));
    }
    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteFaq(String token, Long faqId) {
        validateAdmin(token);

        FaqEntity faq = faqRepository.findById(faqId).orElse(null);
        if (faq == null) {
            return ResponseEntity.ok(new ApiResponse<>(404, false, "FAQ글이 존재하지 않습니다", null));
        }

        faqRepository.delete(faq);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "FAQ 삭제 완료", null));
    }

    public void validateAdmin(String token) {
        Long userId = jwtTokenValidator.getUserId(token);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        if (!user.getIsAdmin()) {
            throw new CustomException(ErrorException.NO_PERMISSION);
        }
    }
}
