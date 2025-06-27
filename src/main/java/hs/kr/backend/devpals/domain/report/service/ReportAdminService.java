package hs.kr.backend.devpals.domain.report.service;

import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.report.dto.ReportDetailResponse;
import hs.kr.backend.devpals.domain.report.dto.ReportSummaryResponse;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.report.repository.ReportRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportAdminService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FaqAdminService faqAdminService;

    public ResponseEntity<ApiResponse<List<ReportSummaryResponse>>> getAllReports(String token) {
        faqAdminService.validateAdmin(token);

        List<ReportEntity> reports = reportRepository.findAll();
        List<ReportSummaryResponse> result = reports.stream().map(report -> {
            UserEntity user = userRepository.findById(report.getReportTargetId())
                    .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
            boolean isImposed = user.getWarning() > 0;
            return ReportSummaryResponse.fromEntity(report, user, isImposed);
        }).toList();

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

}
