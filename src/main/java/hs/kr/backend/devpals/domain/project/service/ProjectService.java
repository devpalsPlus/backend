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
import hs.kr.backend.devpals.global.common.enums.MethodType;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

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

    public ResponseEntity<ApiCustomResponse<List<ProjectAllDto>>> getProjectAll(List<Long> skillTagId, Long positionTagId,
                                                                                MethodType methodType, Boolean isBeginner,
                                                                                String keyword, int page, int size){

        projectAllCache.clear();

        List<ProjectEntity> projects = projectRepository.findAll();

        projects.forEach(project -> {
            if (!projectAllCache.containsKey(project.getId())) {
                UserEntity user = userRepository.findById(project.getAuthorId())
                        .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

                List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTagsAsList());
                List<PositionTagResponse> positionResponses = getPositionTagResponses(project.getPositionTagsAsList());

                ProjectAllDto projectDto = ProjectAllDto.fromEntity(project, user.getNickname(), user.getProfileImg(), skillResponses, positionResponses);

                projectAllCache.put(project.getId(), projectDto);
            }
        });

        //
        List<ProjectAllDto> filteredProjects = projectAllCache.values().stream()
                .filter(project -> (methodType == null || project.getMethodType() == methodType))
                .filter(project -> (isBeginner == null || project.getIsBeginner() == isBeginner))
                .filter(project -> (keyword == null || keyword.isEmpty() ||
                        project.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        project.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(project -> (positionTagId == null || positionTagId == 0 ||
                        project.getPositions().stream().anyMatch(p -> p.getId().equals(positionTagId))))
                .filter(project -> (skillTagId == null || skillTagId.isEmpty() ||
                        project.getSkills().stream().anyMatch(s -> skillTagId.contains(s.getId()))))
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 목록 조회 성공", filteredProjects));
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

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 업데이트 완료", updatedProject));
    }


    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiCustomResponse<Long>> projectSignup(ProjectAllDto request, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(request.getPositions());

        ProjectEntity project = ProjectEntity.fromRequest(request, positionResponses, userId, skillResponses);
        ProjectEntity savedProject = projectRepository.save(project);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 등록 완료", savedProject.getId()));
    }


    // 프로젝트 목록 조회
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> getProjectList(Long projectId) {
        ProjectAllDto project = projectAllCache.get(projectId);

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
