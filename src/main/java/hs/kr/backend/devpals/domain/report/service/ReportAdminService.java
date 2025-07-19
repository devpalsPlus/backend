package hs.kr.backend.devpals.domain.report.service;

import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.report.dto.ReportDetailResponse;
import hs.kr.backend.devpals.domain.report.dto.ReportSummaryResponse;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.report.entity.ReportTagEntity;
import hs.kr.backend.devpals.domain.report.repository.ReportRepository;
import hs.kr.backend.devpals.domain.report.repository.ReportTagRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.ReportFilter;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportAdminService {
    private final ReportRepository reportRepository;
    private final ReportTagRepository reportTagRepository;
    private final UserRepository userRepository;
    private final FaqAdminService faqAdminService;

    public ResponseEntity<ApiResponse<List<ReportSummaryResponse>>> getAllReports(String token) {
        faqAdminService.validateAdmin(token);

        List<ReportEntity> reports = reportRepository.findAll();

        List<ReportSummaryResponse> result = reports.stream()
                .map(report -> {
                    UserEntity user = userRepository.findById(report.getReportTargetId())
                            .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
                    return ReportSummaryResponse.fromEntity(report, user);
                })
                .toList();

        ApiResponse<List<ReportSummaryResponse>> response =
                new ApiResponse<>(200, true, "신고 목록 조회 성공", result);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<ReportDetailResponse>> getReportDetail(Long reportId, String token) {
        faqAdminService.validateAdmin(token);

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorException.REPORT_NOT_FOUND));

        UserEntity reportedUser = userRepository.findById(report.getReportTargetId())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserEntity reporterUser = report.getReporter();

        ReportDetailResponse detail = ReportDetailResponse.fromEntity(report, reporterUser, reportedUser);

        ApiResponse<ReportDetailResponse> response =
                new ApiResponse<>(200, true, "신고 상세 조회 성공", detail);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponse<Void>> imposeWarning(Long reportId, String token) {
        faqAdminService.validateAdmin(token);

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorException.REPORT_NOT_FOUND));

        if (report.isImposed()) {
            throw new CustomException(ErrorException.ALREADY_IMPOSED);
        }

        if (report.getReportFilter() != ReportFilter.USER) {
            throw new CustomException(ErrorException.INVALID_REPORT_TYPE);
        }

        UserEntity user = userRepository.findById(report.getReportTargetId())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        user.increaseWarning();
        user.applyBanIfNeededAfterWarning();

        report.impose(); 

        return ResponseEntity.ok(new ApiResponse<>(200, true, "경고 부여 성공", null));
    }



}
