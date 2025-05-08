package hs.kr.backend.devpals.domain.notice.service;

import hs.kr.backend.devpals.domain.faq.service.FaqService;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDTO;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDetailResponse;
import hs.kr.backend.devpals.domain.notice.dto.PrevNextResponse;
import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import hs.kr.backend.devpals.domain.notice.repository.NoticeRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FaqService faqService;

    @Transactional
    public ResponseEntity<ApiResponse<String>> createNotice(String token, NoticeDTO noticeDTO) {
        faqService.validateAdmin(token);

        NoticeEntity newNotice = NoticeEntity.fromDTO(noticeDTO);

        noticeRepository.save(newNotice);
        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 작성 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateNotice(String token, Long noticeId, NoticeDTO noticeDTO) {
        faqService.validateAdmin(token);

        NoticeEntity existingNotice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorException.NOT_FOUND_NOTICE));

        existingNotice.update(noticeDTO.getTitle(), noticeDTO.getContent());
        noticeRepository.save(existingNotice);

        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 업데이트 성공", null));
    }

    public ResponseEntity<ApiResponse<List<NoticeDTO>>> getNotices() {
        List<NoticeDTO> notices = noticeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NoticeDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 전체 목록 가져오기 성공", notices));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(Long noticeId) {
        NoticeEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorException.NOT_FOUND_NOTICE));

        Optional<NoticeEntity> prev = noticeRepository.findFirstByIdLessThanOrderByIdDesc(noticeId);
        Optional<NoticeEntity> next = noticeRepository.findFirstByIdGreaterThanOrderByIdAsc(noticeId);

        NoticeDetailResponse response = NoticeDetailResponse.fromEntity(
                notice,
                prev.map(PrevNextResponse::fromEntity).orElse(null),
                next.map(PrevNextResponse::fromEntity).orElse(null)
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 상세 내용 가져오기 성공", response));
    }
    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteNotice(String token, Long noticeId) {
        faqService.validateAdmin(token);

        NoticeEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorException.NOT_FOUND_NOTICE));

        noticeRepository.delete(notice);
        return ResponseEntity.ok(new ApiResponse<>(true, "공지사항 삭제 성공", null));
    }
}
