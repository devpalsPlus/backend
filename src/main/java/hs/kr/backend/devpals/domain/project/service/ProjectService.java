package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
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
                    List<PositionTagResponse> positionResponses = getPositionTagResponses(project.getPositionTagsAsList());

                    ProjectMainResponse projectResponse = ProjectMainResponse.fromEntity(project, user, skillResponses, positionResponses);
                    projectMainCache.put(project.getId(), projectResponse);
                    ProjectAllDto projectDto = ProjectAllDto.fromEntity(project, user.getNickname(), user.getProfileImg(), skillResponses, positionResponses);
                    projectAllCache.put(project.getId(), projectDto);

                    return projectDto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 목록 조회 성공", projectList));
    }

    // 프로젝트 개수
    public ResponseEntity<ApiCustomResponse<ProjectCountResponse>> getProjectCount() {
        long totalProjectCount = projectRepository.count();
        long ongoingProjectCount = projectRepository.countByIsDoneFalse();
        long endProjectCount = totalProjectCount - ongoingProjectCount;

        ProjectCountResponse responseData = new ProjectCountResponse(totalProjectCount, ongoingProjectCount, endProjectCount);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 개수 조회 성공", responseData));
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

        getSkillTagResponses(request.getSkills());
        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(project.getPositionTagsAsList());

        project.updateProject(request, request.getPositions(), skillResponses);
        projectRepository.save(project);

        ProjectAllDto updatedProject = ProjectAllDto.fromEntity(project, request.getAuthorNickname(),
                                                                request.getAuthorImage(), skillResponses, positionResponses);

        projectMainCache.clear();

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 업데이트 완료", updatedProject));
    }


    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiCustomResponse<Long>> projectSignup(ProjectAllDto request) {
        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(request.getPositions());

        ProjectEntity project = ProjectEntity.fromRequest(request, positionResponses, skillResponses);
        ProjectEntity savedProject = projectRepository.save(project);

        projectMainCache.clear();

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 등록 완료", savedProject.getId()));
    }


    // 프로젝트 목록 조회
    public ResponseEntity<ApiCustomResponse<ProjectMainResponse>> getProjectList(Long projectId) {
        ProjectMainResponse project = projectMainCache.get(projectId);

        if (project == null) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 조회 성공", project));
    }


    public List<SkillTagResponse> getSkillTagResponses(List<SkillTagResponse> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }

        List<Long> skillIds = skills.stream()
                .map(SkillTagResponse::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<SkillTagEntity> skillEntities = userFacade.getSkillTagsByIds(skillIds);

        return skillEntities.stream()
                .map(skill -> new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg()))
                .collect(Collectors.toList());
    }

    public List<PositionTagResponse> getPositionTagResponses(List<PositionTagResponse> positions) {
        if (positions == null || positions.isEmpty()) {
            throw new CustomException(ErrorException.POSITION_NOT_FOUND);
        }

        List<Long> positionIds = positions.stream()
                .map(PositionTagResponse::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<PositionTagEntity> positionEntities = userFacade.getPositionTagByIds(positionIds);

        return positionEntities.stream()
                .map(position -> new PositionTagResponse(position.getId(), position.getName()))
                .collect(Collectors.toList());
    }

}
