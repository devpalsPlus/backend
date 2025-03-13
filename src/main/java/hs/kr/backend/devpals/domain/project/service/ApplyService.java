package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplicantResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplicantResultResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplyRequest;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplyService {

    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ApplicantRepository applicantRepository;

    public ResponseEntity<ApiResponse<String>> projectApply(Long projectId, ProjectApplyRequest request, String token)   {

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        Optional<ApplicantEntity> existingApplicant = applicantRepository.findByUserAndProject(user, project);
        if (existingApplicant.isPresent()) {
            throw new CustomException(ErrorException.ALREADY_APPLIED);
        }

        ApplicantEntity applicant = ApplicantEntity.createApplicant(user, project, request);
        applicantRepository.save(applicant);

        ApiResponse<String> response = new ApiResponse<String>(true, "프로젝트 지원 되었습니다." , null);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<List<ProjectApplicantResponse>>> getProjectApplicants(Long projectId, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if(!Objects.equals(project.getAuthorId(), userId)) throw new CustomException(ErrorException.AUTHOR_ONLY);


        List<ApplicantEntity> applicants = applicantRepository.findByProject(project);

        List<ProjectApplicantResponse> projectApplicants = applicants
                .stream()
                .map(ProjectApplicantResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "공고 지원자 목록 가져오기 성공",projectApplicants));

    }

    public ResponseEntity<ApiResponse<ProjectApplicantResultResponse>> getProjectApplicantResults(Long projectId, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if(!Objects.equals(project.getAuthorId(), userId)) throw new CustomException(ErrorException.AUTHOR_ONLY);


        List<ApplicantEntity> applicants = applicantRepository.findByProject(project);


        return ResponseEntity.ok(new ApiResponse<>(true, "공고 합격자/불합격자 목록 가져오기 성공",ProjectApplicantResultResponse.fromEntity(applicants)));
    }
}
