package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.evaluation.service.EvaluationService;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectAuthoredResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMyResponse;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.facade.ProjectFacade;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProjectService {
    private final Map<Long, List<ProjectMyResponse>> projectMyCache = new HashMap<>();
    private final Map<Long, List<ProjectApplyResponse>> projectMyApplyCache = new HashMap<>();
    private final JwtTokenValidator jwtTokenValidator;
    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserFacade userFacade;
    private final EvaluationService evaluationService;
    private final ProjectFacade projectFacade;

    @Transactional
    public ResponseEntity<ApiResponse<List<ProjectMyResponse>>> getMyParticipatedProjects(String token) {

        Long userId = jwtTokenValidator.getUserId(token);

        if (projectMyCache.containsKey(userId)) {
            List<ProjectMyResponse> cachedProjects = new ArrayList<>(projectMyCache.get(userId));
            return ResponseEntity.ok(new ApiResponse<>(200, true, "내가 참여한 프로젝트 조회 성공", cachedProjects));
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ApplicantEntity> applications = applicantRepository.findByUser(user)
                .stream()
                .filter(application -> application.getStatus() == ApplicantStatus.ACCEPTED)
                .collect(Collectors.toList());

        if (applications.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, true, "참여한 프로젝트가 없습니다.", new ArrayList<>()));
        }

        List<ProjectMyResponse> myProjects = applications.stream()
                .map(application -> {
                    ProjectEntity project = application.getProject();

                    List<Long> skillTagIds = project.getSkillTagsAsList();
                    List<SkillTagResponse> skillResponses = userFacade.getSkillTagResponses(skillTagIds);
                    boolean isAllEvaluated = evaluationService.isAllEvaluated(project.getId());

                    return ProjectMyResponse.fromEntity(project, skillResponses, isAllEvaluated);
                })
                .collect(Collectors.toList());

        projectMyCache.put(userId, myProjects);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "내가 참여한 프로젝트 조회 성공", myProjects));
    }

    public ResponseEntity<ApiResponse<List<ProjectMyResponse>>> getOnlyParticipatedProjects(String token, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ProjectMyResponse> acceptedProjects = applicantRepository.findByUser(user).stream()
                .filter(app -> app.getStatus() == ApplicantStatus.ACCEPTED)
                .map(app -> {
                    ProjectEntity project = app.getProject();
                    List<SkillTagResponse> skills = userFacade.getSkillTagResponses(project.getSkillTagIds());
                    boolean isAllEvaluated = evaluationService.isAllEvaluated(project.getId());
                    return ProjectMyResponse.fromEntity(project, skills, isAllEvaluated);
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "상대방 참여 프로젝트 조회 성공", acceptedProjects));
    }

    public ResponseEntity<ApiResponse<List<ProjectMyResponse>>> getOnlyCreatedProjects(String token, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ProjectMyResponse> createdProjects = projectRepository.findProjectsByUserId(user.getId()).stream()
                .map(project -> {
                    List<SkillTagResponse> skills = userFacade.getSkillTagResponses(project.getSkillTagIds());
                    boolean isAllEvaluated = evaluationService.isAllEvaluated(project.getId());
                    return ProjectMyResponse.fromEntity(project, skills, isAllEvaluated);
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "상대방이 만든 프로젝트 조회 성공", createdProjects));
    }


    @Transactional
    public ResponseEntity<ApiResponse<List<ProjectApplyResponse>>> getMyProjectApply(String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        if (projectMyApplyCache.containsKey(userId)) {
            return ResponseEntity.ok(new ApiResponse<>(
                    200, true, "내 지원 프로젝트 조회 성공", new ArrayList<>(projectMyApplyCache.get(userId))));
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ApplicantEntity> applications = applicantRepository.findByUser(user);

        if (applications.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, true, "지원한 프로젝트가 없습니다.", new ArrayList<>()));
        }

        List<ProjectApplyResponse> myProjects = applications.stream()
                .map(application -> ProjectApplyResponse.fromEntity(
                        application.getProject().getId(),
                        application.getProject().getTitle(),
                        application.getStatus()
                ))
                .collect(Collectors.toList());

        projectMyApplyCache.put(userId, myProjects);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "내 지원 프로젝트 조회 성공", myProjects));
    }

    public ResponseEntity<ApiResponse<List<ProjectAuthoredResponse>>> getMyProject(String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<ProjectEntity> projects = projectRepository.findProjectsByUserId(userId);

        List<ProjectAuthoredResponse> projectAuthoredResponses = projects.stream()
                .map(project -> {
                    boolean isAllEvaluated = evaluationService.isAllEvaluated(project.getId());

                    return ProjectAuthoredResponse.fromEntity(
                            project,
                            userFacade.getPositionTagByIds(project.getPositionTagIds()),
                            userFacade.getSkillTagsByIds(project.getSkillTagIds()),
                            projectFacade.getMethodTypeById(project.getMethodTypeId()),
                            isAllEvaluated
                    );
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "내 프로젝트 조회 성공", projectAuthoredResponses));
    }
}
