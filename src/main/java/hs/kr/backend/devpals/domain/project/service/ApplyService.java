package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ApplyService {

    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ApplicantRepository applicantRepository;
    private final UserFacade userFacade;

    // 프로젝트 지원하기
    public ResponseEntity<ApiResponse<String>> projectApply(Long projectId, ProjectApplyDTO request, String token)   {

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

        return ResponseEntity.ok(new ApiResponse<>(true, "프로젝트 지원 되었습니다." , null));
    }

    // 프로젝트의 지원자 목록 가져오기
    public ResponseEntity<ApiResponse<List<ProjectApplicantResponse>>> getProjectApplicantList(Long projectId, String token) {
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

    public ResponseEntity<ApiResponse<ProjectApplyDTO>> getProjectApplicantContent(Long projectId, Long applicantId, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if (!Objects.equals(project.getAuthorId(), userId)) {
            throw new CustomException(ErrorException.AUTHOR_ONLY);
        }

        UserEntity user = userRepository.findById(applicantId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        ApplicantEntity applicant = applicantRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new CustomException(ErrorException.INVALID_APPLICANT_PROJECT));

        List<SkillTagResponse> skillResponses = userFacade.getSkillTagResponses(project.getSkillTagIds());

        ProjectApplyDTO dto = ProjectApplyDTO.fromEntity(user, applicant, skillResponses);

        return ResponseEntity.ok(new ApiResponse<>(true, "지원서 조회 성공", dto));
    }

    // 프로젝트의 합격/불합격 목록 가져오기
    public ResponseEntity<ApiResponse<ProjectApplicantResultResponse>> getProjectApplicantResults(Long projectId, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if(!Objects.equals(project.getAuthorId(), userId)) throw new CustomException(ErrorException.AUTHOR_ONLY);


        List<ApplicantEntity> applicants = applicantRepository.findByProject(project);


        return ResponseEntity.ok(new ApiResponse<>(true, "공고 합격자/불합격자 목록 가져오기 성공",ProjectApplicantResultResponse.fromEntity(applicants,userFacade)));
    }

    // 프로젝트 지원한 지원자의 상태 변경하기
    @Transactional
    public ResponseEntity<ApiResponse<ApplicantStatusUpdateResponse>> modifyApplicantStatus(Long projectId, String token, ApplicantStatusUpdateRequest applicantStatusUpdateRequest) {
        Long userId = jwtTokenValidator.getUserId(token);
        String status = applicantStatusUpdateRequest.getStatus();
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        UserEntity user = userRepository.findById(applicantStatusUpdateRequest.getApplicantUserId())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        ApplicantEntity applicant = applicantRepository.findByUserAndProject(user,project).orElseThrow(() -> new CustomException(ErrorException.INVALID_APPLICANT_PROJECT));

        //프로젝트 모집을 마감했을 경우 변경불가
        if(project.isDone()) throw new CustomException(ErrorException.PROJECT_DONE);
        //해당 공고의 기획자가 아닐 경우 조회 불가
        if(!Objects.equals(project.getAuthorId(), userId)) throw new CustomException(ErrorException.AUTHOR_ONLY);
        //변경하려는 상태와 현재 상태가 동일할 경우 상태 변경 불가
        if(applicant.getStatus().toString().equals(status))
            throw new CustomException(ErrorException.EQUAL_STATUS);

        ApplicantStatus applicantStatus = ApplicantStatus.fromString(status).orElseThrow(() -> new CustomException(ErrorException.STATUS_NOT_FOUND));

        applicant.updateStatus(applicantStatus);

        return ResponseEntity.ok(new ApiResponse<>(true, "지원자의 상태 변경 성공", ApplicantStatusUpdateResponse.fromEntity(applicant)));

    }

}
