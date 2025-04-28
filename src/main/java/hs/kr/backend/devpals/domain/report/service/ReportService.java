package hs.kr.backend.devpals.domain.report.service;

import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.Inquiry.repository.InquiryRepository;
import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.entity.RecommentEntity;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.project.repository.CommentRepoisitory;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.project.repository.RecommentRepository;
import hs.kr.backend.devpals.domain.report.dto.ReportRequest;
import hs.kr.backend.devpals.domain.report.dto.ReportResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.report.repository.ReportRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.domain.user.service.AlarmService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.ReportFilter;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    public static final int REPORT_COUNT = 1;
    private final ReportRepository reportRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepoisitory commentRepoisitory;
    private final RecommentRepository recommentRepository;
    private final InquiryRepository inquiryRepository;
    private final AlarmService alarmService;

    @Transactional
    public ResponseEntity<ApiResponse<ReportResponse>> report(ReportRequest request, String token) {
        Long userId = jwtTokenValidator.getUserId(token);
        UserEntity reporter = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        validation(request.getReportFilter(), request.getReportTargetId());
        ReportEntity reportEntity = new ReportEntity(request, reporter);
        ReportEntity savedReport = reportRepository.save(reportEntity);
        // 신고 대상 엔티티 및 경고 처리
        processWarning(savedReport);

        return ResponseEntity.ok(new ApiResponse<>(true, "신고 작성 성공", ReportResponse.of(savedReport)));

    }

    private void processWarning(ReportEntity report) {
        // 신고 대상에 따른 처리
        switch (report.getReportFilter()) {
            case USER -> processUserWarning(report);
            case PROJECT -> processProjectWarning(report);
            case COMMENT -> processCommentWarning(report);
            case RECOMMENT -> processRecommentWarning(report);
            case INQUIRY -> processInquiryWarning(report);
            default -> throw new IllegalArgumentException("지원하지 않는 신고 유형입니다: " + report.getReportFilter());
        }
    }

    private void processUserWarning(ReportEntity report) {
        Long targetId = report.getReportTargetId();
        UserEntity user = userRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ReportEntity> reports = reportRepository.findByReportFilterAndReportTargetId(
                ReportFilter.USER, targetId);

        if (reports.size() >= REPORT_COUNT) {
            // 경고 카운트 증가
            user.increaseWarning();
            userRepository.save(user);
            alarmService.sendReportAlarm(user,report);

        }
    }

    // 다른 엔티티 타입에 대한 유사한 메서드들...
    private void processProjectWarning(ReportEntity report) {
        Long targetId = report.getReportTargetId();
        ProjectEntity project = projectRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        List<ReportEntity> reports = reportRepository.findByReportFilterAndReportTargetId(
                ReportFilter.PROJECT, targetId);

        if (reports.size() >= REPORT_COUNT) {
            project.increaseWarning();
            projectRepository.save(project);
            alarmService.sendReportAlarm(project,report);

        }
    }

    private void processCommentWarning(ReportEntity report) {
        Long targetId = report.getReportTargetId();
        CommentEntity comment = commentRepoisitory.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorException.COMMENT_NOT_FOUND));

        List<ReportEntity> reports = reportRepository.findByReportFilterAndReportTargetId(
                ReportFilter.COMMENT, targetId);

        if (reports.size() >= REPORT_COUNT) {
            // 댓글의 경고 카운트 증가
            comment.increaseWarning();
            commentRepoisitory.save(comment);
            alarmService.sendReportAlarm(comment,report);
        }
    }

    private void processRecommentWarning(ReportEntity report) {
        Long targetId = report.getReportTargetId();
        RecommentEntity recomment = recommentRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorException.RECOMMENT_NOT_FOUND));

        List<ReportEntity> reports = reportRepository.findByReportFilterAndReportTargetId(
                ReportFilter.RECOMMENT, targetId);

        if (reports.size() >= REPORT_COUNT) {
            // 대댓글의 경고 카운트 증가
            recomment.increaseWarning();
            recommentRepository.save(recomment);
            alarmService.sendReportAlarm(recomment,report);

        }
    }

    private void processInquiryWarning(ReportEntity report) {
        Long targetId = report.getReportTargetId();
        InquiryEntity inquiry = inquiryRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));

        List<ReportEntity> reports = reportRepository.findByReportFilterAndReportTargetId(
                ReportFilter.INQUIRY, targetId);

        if (reports.size() >= REPORT_COUNT) {
            // 문의의 경고 카운트 증가
            inquiry.increaseWarning();
            inquiryRepository.save(inquiry);
            alarmService.sendReportAlarm(inquiry,report);
        }
    }


    private void validation(Integer reportFilter, Long id) {
        if(reportFilter.equals(ReportFilter.USER.getValue()))
            userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        if(reportFilter.equals(ReportFilter.PROJECT.getValue()))
            projectRepository.findById(id).orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));
        if(reportFilter.equals(ReportFilter.COMMENT.getValue()))
            commentRepoisitory.findById(id).orElseThrow(() -> new CustomException(ErrorException.COMMENT_NOT_FOUND));
        if(reportFilter.equals(ReportFilter.RECOMMENT.getValue()))
            recommentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorException.RECOMMENT_NOT_FOUND));
        if(reportFilter.equals(ReportFilter.INQUIRY.getValue()))
            inquiryRepository.findById(id).orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));
    }

}
