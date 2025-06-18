package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.project.dto.FullApplicantInfoResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplicantResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplicantResultResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplyDTO;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.tag.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.tag.service.TagService;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectAdminService {

    private final ProjectRepository projectRepository;
    private final ApplicantRepository applicantRepository;
    private final TagService tagService;
    private final FaqAdminService faqAdminService;

    public ResponseEntity<ApiResponse<FullApplicantInfoResponse>> getFullApplicantInfo(Long projectId, Long applicantId, String token) {

        faqAdminService.validateAdmin(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        List<ApplicantEntity> applicants = applicantRepository.findByProject(project);

        List<ProjectApplicantResponse> applicantResponses = applicants.stream()
                .map(ProjectApplicantResponse::fromEntity)
                .collect(Collectors.toList());

        ApplicantEntity targetApplicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        if (!Objects.equals(targetApplicant.getProject().getId(), projectId)) {
            throw new CustomException(ErrorException.INVALID_APPLICANT_PROJECT);
        }

        UserEntity user = targetApplicant.getUser();
        List<SkillTagResponse> skillResponses = tagService.getSkillTagResponses(project.getSkillTagIds());
        ProjectApplyDTO detail = ProjectApplyDTO.fromEntity(user, targetApplicant, skillResponses);

        List<ProjectApplicantResultResponse> resultResponses = List.of(
                ProjectApplicantResultResponse.fromEntity(applicants, tagService)
        );

        FullApplicantInfoResponse response = FullApplicantInfoResponse.From(applicantResponses, detail, resultResponses);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "프로젝트 관련 조회 성공 (관리자용)" , response));
    }


}
