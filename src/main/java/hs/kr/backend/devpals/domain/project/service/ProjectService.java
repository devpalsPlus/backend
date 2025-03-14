package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserFacade userFacade;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;

    private final Map<Long, ProjectAllDto> projectAllCache = new HashMap<>();
    private final Map<Long, ProjectMainResponse> projectMainCache = new HashMap<>();

    public ResponseEntity<ApiCustomResponse<List<ProjectAllDto>>> getProjectAll() {

        List<ProjectEntity> projects = projectRepository.findAll();

        List<ProjectAllDto> projectList = projects.stream()
                .map(project -> {
                    UserEntity user = userRepository.findById(project.getAuthorId())
                            .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

                    List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTagsAsList());
                    ProjectAllDto allDto = ProjectAllDto.fromEntity(project, user.getNickname(), user.getProfileImg(), skillResponses);
                    projectAllCache.put(project.getId(), allDto);
                    ProjectMainResponse mainResponse = ProjectMainResponse.fromEntity(project, user, skillResponses);
                    projectMainCache.put(project.getId(), mainResponse);

                    return allDto;
                })
                .collect(Collectors.toList());

        ApiCustomResponse<List<ProjectAllDto>> response = new ApiCustomResponse<>(true, "프로젝트 목록 조회 성공", projectList);
        return ResponseEntity.ok(response);
    }


    // 프로젝트 개수
    public ResponseEntity<ApiCustomResponse<ProjectCountResponse>> getProjectCount() {
        long totalProjectCount = projectRepository.count();
        long ongoingProjectCount = projectRepository.countByIsDoneFalse();
        long endProjectCount = totalProjectCount - ongoingProjectCount;

        ProjectCountResponse responseData = new ProjectCountResponse(totalProjectCount, ongoingProjectCount, endProjectCount);

        ApiCustomResponse<ProjectCountResponse> response = new ApiCustomResponse<>(true, "프로젝트 개수 조회 성공", responseData);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 업데이트
    @Transactional
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> updateProject(Long projectId, String token, ProjectAllDto request) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if (!project.getAuthorId().equals(userId)) {
            throw new CustomException(ErrorException.FAIL_PROJECT_UPDATE);
        }

        validateSkillsExistence(request.getSkills()); // 스킬 검증
        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());

        project.updateProject(request, request.getPositions(), skillResponses);
        projectRepository.save(project);

        ProjectAllDto updatedProject = ProjectAllDto.fromEntity(project, request.getAuthorNickname(), request.getAuthorImage(), skillResponses);

        projectAllCache.clear();
        projectMainCache.clear();

        ApiCustomResponse<ProjectAllDto> response = new ApiCustomResponse<>(true, "프로젝트 업데이트 완료", updatedProject);
        return ResponseEntity.ok(response);
    }


    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiCustomResponse<Long>> projectSignup(ProjectAllDto request) {

        validateSkillsExistence(request.getSkills());

        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());
        ProjectEntity project = ProjectEntity.fromRequest(request, request.getPositions(), skillResponses);
        ProjectEntity savedProject = projectRepository.save(project);

        ApiCustomResponse<Long> response = new ApiCustomResponse<>(true, "프로젝트 등록 완료", savedProject.getId());
        return ResponseEntity.ok(response);
    }


    // 프로젝트 목록 조회
    public ResponseEntity<ApiCustomResponse<ProjectMainResponse>> getProjectList(Long projectId) {
        ProjectMainResponse project = projectMainCache.get(projectId);

        if (project == null) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        ApiCustomResponse<ProjectMainResponse> response = new ApiCustomResponse<>(true, "프로젝트 조회 성공", project);
        return ResponseEntity.ok(response);
    }



    private void validateSkillsExistence(List<SkillTagResponse> skills) {
        List<String> skillNames = skills.stream()
                .map(SkillTagResponse::getSkillName)
                .distinct()
                .collect(Collectors.toList());

        List<SkillTagEntity> skillEntities = userFacade.getSkillTagsByNames(skillNames);

        if (skillEntities.size() != skillNames.size()) {
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }
    }

    private List<SkillTagResponse> getSkillTagResponses(List<SkillTagResponse> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> skillNames = skills.stream()
                .map(SkillTagResponse::getSkillName)
                .collect(Collectors.toList());

        List<SkillTagEntity> skillEntities = userFacade.getSkillTagsByNames(skillNames);

        return skillEntities.stream()
                .map(skill -> new SkillTagResponse(skill.getName(), skill.getImg()))
                .collect(Collectors.toList());
    }

}
