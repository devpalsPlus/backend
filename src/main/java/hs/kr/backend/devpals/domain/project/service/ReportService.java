package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectAllDto;
import hs.kr.backend.devpals.domain.project.entity.ReportEntity;
import hs.kr.backend.devpals.domain.project.repository.CommentRepoisitory;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.project.repository.RecommentRepository;
import hs.kr.backend.devpals.domain.user.dto.ReportRequest;
import hs.kr.backend.devpals.domain.user.dto.ReportResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.ReportRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.ReportFilter;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepoisitory commentRepoisitory;
    private final RecommentRepository recommentRepository;

    public ResponseEntity<ApiResponse<ReportResponse>> report(ReportRequest request, String token) {
        Long userId = jwtTokenValidator.getUserId(token);
        UserEntity reporter = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        validation(request.getReportFilter(), request.getReportTargetId());
        ReportEntity reportEntity = new ReportEntity(request, reporter);
        ReportEntity savedReport = reportRepository.save(reportEntity);

        return ResponseEntity.ok(new ApiResponse<>(true, "프로젝트 업데이트 완료", ReportResponse.of(savedReport)));

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
        //TODO: 문의 내용 추가시 추가 필요
    }
}
