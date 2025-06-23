package hs.kr.backend.devpals.domain.Inquiry.service;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryAnswerRequest;
import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryPreviewResponse;
import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.Inquiry.repository.InquiryRepository;
import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.user.service.AlarmService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryAdminService {
    private final InquiryRepository inquiryRepository;
    private final FaqAdminService faqAdminService;
    private final AlarmService alarmService;


    @Transactional
    public ResponseEntity<ApiResponse<String>> registerAnswer(String token, Long inquiryId, InquiryAnswerRequest answer) {
        faqAdminService.validateAdmin(token);

        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));

        if (inquiry.getAnswer() != null) {
            throw new CustomException(ErrorException.ALREADY_ANSWERED);
        }

        inquiry.writeAnswer(answer.getAnswer());
        alarmService.sendAlarm(inquiry);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "답변 등록 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateAnswer(String token, Long inquiryId, InquiryAnswerRequest answer) {
        faqAdminService.validateAdmin(token);

        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));

        if (inquiry.getAnswer() == null) {
            throw new CustomException(ErrorException.ANSWER_NOT_FOUND);
        }

        inquiry.updateAnswer(answer.getAnswer());

        return ResponseEntity.ok(new ApiResponse<>(200, true, "답변 수정 성공", null));
    }

    public ResponseEntity<ApiResponse<List<InquiryPreviewResponse>>> getAllInquiries(
            Long userId, LocalDate startDate, LocalDate endDate) {

        List<InquiryEntity> inquiries;

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);

            if (userId != null) {
                inquiries = inquiryRepository.findInquiriesByUserIdAndCreatedAtBetween(userId, start, end);
            } else {
                inquiries = inquiryRepository.findInquiriesByDate(start, end);
            }
        } else {
            inquiries = (userId != null)
                    ? inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId)
                    : inquiryRepository.findAllByOrderByCreatedAtDesc();
        }

        List<InquiryPreviewResponse> responses = inquiries.stream()
                .map(InquiryPreviewResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "문의글 조회 성공", responses));
    }



    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<InquiryResponse>> getInquiryDetail(Long inquiryId) {
        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));

        InquiryResponse inquiryResponse = InquiryResponse.fromEntity(inquiry);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "문의 상세 조회 성공", inquiryResponse));
    }
}
