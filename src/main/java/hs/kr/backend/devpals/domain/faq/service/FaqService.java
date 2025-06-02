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

    public ResponseEntity<ApiResponse<List<FaqDTO>>> getAllFaq(String keyword) {
        List<FaqEntity> faqs = keyword.isBlank()
                ? faqRepository.findAllByOrderByCreatedAtDesc()
                : faqRepository.findByTitleContainingOrderByCreatedAtDesc(keyword);

        List<FaqDTO> result = faqs.stream()
                .map(FaqDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(200, true, "FAQ 조회 성공", result));
    }


    public ResponseEntity<ApiResponse<FaqDTO>> getFaq(Long faqId) {

        return faqRepository.findById(faqId)
                .map(faq -> {
                    FaqDTO responseDto = FaqDTO.fromEntity(faq);
                    return ResponseEntity.ok(new ApiResponse<>(200, true, "FAQ 상세 조회 성공", responseDto));
                })
                .orElseGet(() ->
                        ResponseEntity.ok(new ApiResponse<>(404, false, "FAQ글이 존재하지 않습니다", null))
                );
    }

}
