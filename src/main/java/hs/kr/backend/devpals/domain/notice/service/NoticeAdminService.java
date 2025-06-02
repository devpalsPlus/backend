package hs.kr.backend.devpals.domain.notice.service;

import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDTO;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDetailResponse;
import hs.kr.backend.devpals.domain.notice.dto.NoticeListResponse;
import hs.kr.backend.devpals.domain.notice.dto.PrevNextResponse;
import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import hs.kr.backend.devpals.domain.notice.repository.NoticeRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
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
public class NoticeAdminService {

    private final NoticeRepository noticeRepository;
    private final FaqAdminService faqAdminService;

    @Transactional
    public ResponseEntity<ApiResponse<String>> createNotice(String token, NoticeDTO noticeDTO) {
        faqAdminService.validateAdmin(token);

        NoticeEntity newNotice = NoticeEntity.fromDTO(noticeDTO);

        noticeRepository.save(newNotice);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "공지사항 작성 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateNotice(String token, Long noticeId, NoticeDTO noticeDTO) {
        faqAdminService.validateAdmin(token);

        NoticeEntity existingNotice = noticeRepository.findById(noticeId).orElse(null);
        if (existingNotice == null) {
            return ResponseEntity.ok(new ApiResponse<>(404, false, "공지사항글이 존재하지 않습니다", null));
        }

        existingNotice.update(noticeDTO.getTitle(), noticeDTO.getContent());
        noticeRepository.save(existingNotice);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "공지사항 업데이트 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteNotice(String token, Long noticeId) {
        faqAdminService.validateAdmin(token);

        NoticeEntity notice = noticeRepository.findById(noticeId).orElse(null);
        if (notice == null) {
            return ResponseEntity.ok(new ApiResponse<>(404, false, "공지사항글이 존재하지 않습니다", null));
        }

        noticeRepository.delete(notice);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "공지사항 삭제 성공", null));
    }

}
