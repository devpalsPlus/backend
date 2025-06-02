package hs.kr.backend.devpals.domain.notice.service;

import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.faq.service.FaqService;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDTO;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDetailResponse;
import hs.kr.backend.devpals.domain.notice.dto.NoticeListResponse;
import hs.kr.backend.devpals.domain.notice.dto.PrevNextResponse;
import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import hs.kr.backend.devpals.domain.notice.repository.NoticeRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public ResponseEntity<ApiResponse<NoticeListResponse>> getNotices(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NoticeEntity> noticePage = (keyword == null || keyword.isBlank())
                ? noticeRepository.findAll(pageable)
                : noticeRepository.findByTitleContainingIgnoreCase(keyword, pageable);

        NoticeListResponse response = NoticeListResponse.from(noticePage.getContent(), noticePage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(200, true, "공지사항 전체 목록 가져오기 성공", response));
    }

    @Transactional
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(Long noticeId) {
        NoticeEntity notice = noticeRepository.findById(noticeId).orElse(null);
        if (notice == null) {
            return ResponseEntity.ok(new ApiResponse<>(404,false, "공지사항글이 존재하지 않습니다", null));
        }

        notice.increaseViewCount();

        Optional<NoticeEntity> prev = noticeRepository.findFirstByIdLessThanOrderByIdDesc(noticeId);
        Optional<NoticeEntity> next = noticeRepository.findFirstByIdGreaterThanOrderByIdAsc(noticeId);

        NoticeDetailResponse response = NoticeDetailResponse.fromEntity(
                notice,
                prev.map(PrevNextResponse::fromEntity).orElse(null),
                next.map(PrevNextResponse::fromEntity).orElse(null)
        );

        return ResponseEntity.ok(new ApiResponse<>(200, true, "공지사항 상세 내용 가져오기 성공", response));
    }

}
