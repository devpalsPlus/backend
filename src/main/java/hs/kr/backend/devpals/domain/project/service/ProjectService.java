package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectRequest;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PositionTagRepository positionTagRepository;
    private final SkillTagRepository skillTagRepository;
    private final JwtTokenValidator jwtTokenValidator;

    @Transactional
    public ResponseEntity<ApiResponse<Long>> projectSignup(ProjectRequest request) {

        List<String> positionTagNames = convertPositionTagIdsToNames(request.getPositionTagIds());
        List<String> skillTagNames = convertSkillTagIdsToNames(request.getSkillTagIds());

        ProjectEntity project = ProjectEntity.fromRequest(request, positionTagNames, skillTagNames);

        ProjectEntity savedProject = projectRepository.save(project);
        ApiResponse<Long> response = new ApiResponse<>(true, "프로젝트 등록 완료", savedProject.getId());
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateProject(Long projectId, String token, ProjectRequest request) {

        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if (!project.getAuthorId().equals(userId)) {
            throw new CustomException(ErrorException.FAIL_PROJECT_UPDATE);
        }

        List<String> positionTagNames = convertPositionTagIdsToNames(request.getPositionTagIds());
        List<String> skillTagNames = convertSkillTagIdsToNames(request.getSkillTagIds());

        project.updateProject(request, positionTagNames, skillTagNames);

        projectRepository.save(project);
        ApiResponse<String> response = new ApiResponse<>(true, "프로젝트 업데이트 완료", null);
        return ResponseEntity.ok(response);
    }

    // positionTagIds를 태그 이름으로 변환
    private List<String> convertPositionTagIdsToNames(List<Long> positionTagIds) {
        return positionTagIds.stream()
                .map(id -> positionTagRepository.findById(id)
                        .map(PositionTagEntity::getName)
                        .orElseThrow(() -> new CustomException(ErrorException.POSITION_NOT_FOUND)))
                .collect(Collectors.toList());
    }

    // skillTagIds를 태그 이름으로 변환
    private List<String> convertSkillTagIdsToNames(List<Long> skillTagIds) {
        return skillTagIds.stream()
                .map(id -> skillTagRepository.findById(id)
                        .map(SkillTagEntity::getName)
                        .orElseThrow(() -> new CustomException(ErrorException.SKILL_NOT_FOUND)))
                .collect(Collectors.toList());
    }
}