package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
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
    private final SkillTagRepository skillTagRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;

    private final Map<Long, ProjectMainResponse> projectMainCache = new HashMap<>();
    private final Map<Long, ProjectMineResponse> projectMyCache = new HashMap<>();

    public ResponseEntity<ApiResponse<List<ProjectAllDto>>> getProjectAll() {

        List<ProjectEntity> projects = projectRepository.findAll();

        List<ProjectAllDto> projectList = projects.stream()
                .map(project -> {
                    UserEntity user = userRepository.findById(project.getAuthorId())
                            .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

                    List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTagsAsList());
                    ProjectAllDto response = ProjectAllDto.fromEntity(project, user.getNickname(), user.getProfileImg(), skillResponses);
                    ProjectMainResponse mainResponse = ProjectMainResponse.fromEntity(project, user, skillResponses);
                    projectMainCache.put(project.getId(), mainResponse);

                    return response;
                })
                .collect(Collectors.toList());

        ApiResponse<List<ProjectAllDto>> response = new ApiResponse<>(true, "프로젝트 목록 조회 성공", projectList);
        return ResponseEntity.ok(response);
    }


    // 프로젝트 개수
    public ResponseEntity<ApiResponse<ProjectCountResponse>> getProjectCount() {
        long totalProjectCount = projectRepository.count();
        long ongoingProjectCount = projectRepository.countByIsDoneFalse();
        long endProjectCount = totalProjectCount - ongoingProjectCount;

        ProjectCountResponse responseData = new ProjectCountResponse(totalProjectCount, ongoingProjectCount, endProjectCount);

        ApiResponse<ProjectCountResponse> response = new ApiResponse<>(true, "프로젝트 개수 조회 성공", responseData);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 업데이트
    @Transactional
    public ResponseEntity<ApiResponse<ProjectAllDto>> updateProject(Long projectId, String token, ProjectAllDto request) {
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

        projectMainCache.clear();
        projectMyCache.clear();

        ApiResponse<ProjectAllDto> response = new ApiResponse<>(true, "프로젝트 업데이트 완료", updatedProject);
        return ResponseEntity.ok(response);
    }


    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiResponse<Long>> projectSignup(ProjectAllDto request) {

        validateSkillsExistence(request.getSkills());

        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());
        ProjectEntity project = ProjectEntity.fromRequest(request, request.getPositions(), skillResponses);
        ProjectEntity savedProject = projectRepository.save(project);

        ApiResponse<Long> response = new ApiResponse<>(true, "프로젝트 등록 완료", savedProject.getId());
        return ResponseEntity.ok(response);
    }


    // 프로젝트 목록 조회
    public ResponseEntity<ApiResponse<ProjectMainResponse>> getProjectList(Long projectId) {
        ProjectMainResponse project = projectMainCache.get(projectId);

        if (project == null) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        ApiResponse<ProjectMainResponse> response = new ApiResponse<>(true, "프로젝트 조회 성공", project);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponse<List<ProjectMineResponse>>> getMyProject(String token) {

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ApplicantEntity> applications = applicantRepository.findByUser(user);

        if (applications.isEmpty()) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        List<ProjectMineResponse> myProjects = applications.stream()
                .map(application -> {
                    ProjectEntity project = application.getProject();
                    List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTagsAsList());
                    ProjectMineResponse response = ProjectMineResponse.fromEntity(project, skillResponses);

                    projectMyCache.put(project.getId(), response);
                    return response;
                })
                .collect(Collectors.toList());

        ApiResponse<List<ProjectMineResponse>> response = new ApiResponse<>(true, "내가 지원한 프로젝트 조회 성공", myProjects);
        return ResponseEntity.ok(response);
    }


    private void validateSkillsExistence(List<SkillTagResponse> skills) {
        List<String> skillNames = skills.stream()
                .map(SkillTagResponse::getSkillName)
                .distinct()
                .collect(Collectors.toList());

        List<SkillTagEntity> skillEntities = skillTagRepository.findByNameIn(skillNames);

        if (skillEntities.size() != skillNames.size()) {
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }
    }

    /**
     * 📌 스킬 태그를 DB에서 조회하여 매핑
     */
    private Map<String, String> getSkillImageMap(List<String> skillNames) {
        List<SkillTagEntity> skillTagEntities = skillTagRepository.findByNameIn(skillNames);

        if (skillTagEntities.size() != skillNames.size()) {
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }

        return skillTagEntities.stream()
                .collect(Collectors.toMap(SkillTagEntity::getName, SkillTagEntity::getImg));
    }

    /**
     * 📌 스킬 태그 변환 (스킬 목록을 `List<SkillTagResponse>`로 변환)
     */
    private List<SkillTagResponse> getSkillTagResponses(List<SkillTagResponse> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> skillNames = skills.stream()
                .map(SkillTagResponse::getSkillName)
                .collect(Collectors.toList());

        Map<String, String> skillImgMap = getSkillImageMap(skillNames);

        return skills.stream()
                .map(skill -> new SkillTagResponse(skill.getSkillName(),
                        skillImgMap.getOrDefault(skill.getSkillName(), "default-img.png")))
                .collect(Collectors.toList());
    }
}
