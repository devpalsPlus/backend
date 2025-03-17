package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import hs.kr.backend.devpals.global.common.enums.MethodType;
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

    private final Map<Long, ProjectAllDto> projectAllCache = new HashMap<>();

    // 프로젝트 목록 조회
    public ResponseEntity<ApiCustomResponse<List<ProjectAllDto>>> getProjectAll(List<Long> skillTagId, Long positionTagId,
                                                                                MethodType methodType, Boolean isBeginner,
                                                                                String keyword, int page, int size) {

        projectAllCache.clear();

        List<ProjectEntity> projects = projectRepository.findAll();

        projects.forEach(project -> {
            if (!projectAllCache.containsKey(project.getId())) {
                List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTagIds());
                List<PositionTagResponse> positionResponses = getPositionTagResponses(project.getPositionTagIds());

                ProjectAllDto projectDto = ProjectAllDto.fromEntity(project, positionResponses, skillResponses);
                projectAllCache.put(project.getId(), projectDto);
            }
        });

        // 필터링 적용
        List<ProjectAllDto> filteredProjects = projectAllCache.values().stream()
                .filter(project -> (methodType == null || project.getMethodType() == methodType))
                .filter(project -> (isBeginner == null || project.getIsBeginner() == isBeginner))
                .filter(project -> (keyword == null || keyword.isEmpty() ||
                        project.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        project.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(project -> (positionTagId == null || positionTagId == 0 ||
                        project.getPositionTagIds().contains(positionTagId)))
                .filter(project -> (skillTagId == null || skillTagId.isEmpty() ||
                        project.getSkillTagIds().stream().anyMatch(skillTagId::contains)))
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 목록 조회 성공", filteredProjects));
    }

    // 프로젝트 개수 조회
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

        project.updateProject(request);
        projectRepository.save(project);

        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkillTagIds());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(request.getPositionTagIds());

        ProjectAllDto updatedProject = ProjectAllDto.fromEntity(project, positionResponses, skillResponses);
        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 업데이트 완료", updatedProject));
    }

    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> projectSignup(ProjectAllDto request, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = ProjectEntity.fromRequest(request, userId);
        ProjectEntity savedProject = projectRepository.save(project);

        List<SkillTagResponse> skillResponses = getSkillTagResponses(savedProject.getSkillTagIds());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(savedProject.getPositionTagIds());

        ProjectAllDto responseDto = ProjectAllDto.fromEntity(savedProject, positionResponses, skillResponses);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 등록 완료", responseDto));
    }


    // 특정 프로젝트 조회
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> getProjectList(Long projectId) {
        ProjectAllDto project = projectAllCache.get(projectId);
        if (project == null) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }
        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 조회 성공", project));
    }

    // 스킬 태그 변환 (ID 리스트 -> DTO 리스트)
    private List<SkillTagResponse> getSkillTagResponses(List<Long> skillTagIds) {
        if (skillTagIds == null || skillTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userFacade.getSkillTagsByIds(skillTagIds).stream()
                .map(skill -> new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg()))
                .collect(Collectors.toList());
    }


    // 포지션 태그 변환 (ID 리스트 -> DTO 리스트)
    private List<PositionTagResponse> getPositionTagResponses(List<Long> positionTagIds) {
        if (positionTagIds == null || positionTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userFacade.getPositionTagByIds(positionTagIds).stream()
                .map(position -> new PositionTagResponse(position.getId(), position.getName()))
                .collect(Collectors.toList());
    }
}
