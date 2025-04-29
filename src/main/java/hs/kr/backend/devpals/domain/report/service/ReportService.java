package hs.kr.backend.devpals.domain.report.service;


import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.entity.RecommentEntity;
import hs.kr.backend.devpals.domain.report.dto.ReportTagDto;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.project.repository.CommentRepoisitory;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.project.repository.RecommentRepository;
import hs.kr.backend.devpals.domain.report.dto.ReportRequest;
import hs.kr.backend.devpals.domain.report.dto.ReportResponse;
import hs.kr.backend.devpals.domain.report.entity.ReportTagEntity;
import hs.kr.backend.devpals.domain.report.facade.ReportFacade;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    public static final int REPORT_COUNT = 1; //현재 테스트용으로 설정
    private final ReportRepository reportRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepoisitory commentRepoisitory;
    private final RecommentRepository recommentRepository;
    private final AlarmService alarmService;
    private final ReportFacade reportFacade;

//    @Transactional
//    public ResponseEntity<ApiResponse<ReportResponse>> report(ReportRequest request, String token) {
//        Long userId = jwtTokenValidator.getUserId(token);
//        UserEntity reporter = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
//        List<ReportTagDto> matchingReportTagDtos = getMatchingReportTags(request);
//        validation(request.getReportFilter(), request.getReportTargetId());
//        ReportEntity reportEntity = new ReportEntity(request, matchingReportTagDtos.stream().map(ReportTagDto::getId).toList(),reporter);
//        ReportEntity savedReport = reportRepository.save(reportEntity);
//        // 신고 대상 엔티티 및 경고 처리
//        processWarning(savedReport);
//
//        return ResponseEntity.ok(new ApiResponse<>(true, "신고 작성 성공", ReportResponse.of(savedReport,matchingReportTagDtos.stream().map(ReportTagDto::getName).toList())));
//
//    }

    @Transactional
    public ResponseEntity<ApiResponse<ReportResponse>> report(ReportRequest request, String token) {
        try {
            log.info("Starting report process with request: {}", request);

            Long userId = jwtTokenValidator.getUserId(token);
            log.info("User ID extracted from token: {}", userId);

            UserEntity reporter = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
            log.info("Reporter found: id={}, nickname={}", reporter.getId(), reporter.getNickname());

            List<ReportTagDto> matchingReportTagDtos = getMatchingReportTags(request);
            log.info("Matching report tags: {}", matchingReportTagDtos);

            validation(request.getReportFilter(), request.getReportTargetId());
            log.info("Validation passed for filter: {} and targetId: {}", request.getReportFilter(), request.getReportTargetId());

            List<Long> tagIds = matchingReportTagDtos.stream().map(ReportTagDto::getId).toList();
            log.info("Extracted tag IDs: {}", tagIds);

            ReportEntity reportEntity = new ReportEntity(request, tagIds, reporter);
            log.info("Created report entity: {}", reportEntity);

            // 디버깅을 위한 엔티티 상세 정보 출력
            log.info("Report details - Filter: {}, TargetId: {}, Reporter: {}, Tags: {}",
                    reportEntity.getReportFilter(),
                    reportEntity.getReportTargetId(),
                    reportEntity.getReporter().getId(),
                    reportEntity.getReportTagIds());

            try {
                ReportEntity savedReport = reportRepository.save(reportEntity);
                log.info("Report successfully saved with ID: {}", savedReport.getId());

                // 신고 대상 엔티티 및 경고 처리
                processWarning(savedReport);
                log.info("Warning process completed");

                return ResponseEntity.ok(new ApiResponse<>(true, "신고 작성 성공",
                        ReportResponse.of(savedReport, matchingReportTagDtos.stream().map(ReportTagDto::getName).toList())));
            } catch (Exception e) {
                log.error("Error saving report entity", e);
                log.error("Exception type: {}", e.getClass().getName());
                log.error("Exception message: {}", e.getMessage());

                // 예외의 원인 추적
                Throwable cause = e.getCause();
                if (cause != null) {
                    log.error("Root cause: {}", cause.getMessage());
                    cause.printStackTrace();
                }

                // 데이터베이스 관련 예외인 경우 더 많은 정보 수집
                if (e instanceof DataAccessException) {
                    log.error("DataAccessException detected: {}", e.getClass().getSimpleName());
                }

                throw e;
            }
        } catch (Exception e) {
            log.error("Unexpected error in report method", e);
            throw e;
        }
    }

    private List<ReportTagDto> getMatchingReportTags(ReportRequest request) {
        Map<String, ReportTagEntity> tagNameMap = reportFacade.getReportTag().stream()
                .collect(Collectors.toMap(ReportTagEntity::getName, tag -> tag));

        List<ReportTagDto> matchingTags = new ArrayList<>(request.getReason().length);

        for (String reason : request.getReason()) {
            ReportTagEntity tag = tagNameMap.get(reason);
            if (tag == null) {
                throw new CustomException(ErrorException.REPORT_TAG_NOT_FOUND);
            }
            matchingTags.add(new ReportTagDto(tag.getId(), tag.getName()));
        }

        return matchingTags;
    }

    private void processWarning(ReportEntity report) {
        // 신고 대상에 따른 처리
        switch (report.getReportFilter()) {
            case USER -> processUserWarning(report);
            case PROJECT -> processProjectWarning(report);
            case COMMENT -> processCommentWarning(report);
            case RECOMMENT -> processRecommentWarning(report);
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


    private void validation(Integer reportFilter, Long id) {
        if(reportFilter.equals(ReportFilter.USER.getValue()))
            userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        if(reportFilter.equals(ReportFilter.PROJECT.getValue()))
            projectRepository.findById(id).orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));
        if(reportFilter.equals(ReportFilter.COMMENT.getValue()))
            commentRepoisitory.findById(id).orElseThrow(() -> new CustomException(ErrorException.COMMENT_NOT_FOUND));
        if(reportFilter.equals(ReportFilter.RECOMMENT.getValue()))
            recommentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorException.RECOMMENT_NOT_FOUND));
   }

}
