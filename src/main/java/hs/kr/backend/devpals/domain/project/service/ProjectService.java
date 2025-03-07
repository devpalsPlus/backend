package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PositionTagRepository positionTagRepository;
    private final SkillTagRepository skillTagRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<ApiResponse<Long>> projectSignup(ProjectAllRequest request) {

        List<String> positionTagNames = convertPositionTagIdsToNames(request.getPositionTagIds());
        List<String> skillTagNames = convertSkillTagIdsToNames(request.getSkillTagIds());

        ProjectEntity project = ProjectEntity.fromRequest(request, positionTagNames, skillTagNames);

        ProjectEntity savedProject = projectRepository.save(project);
        ApiResponse<Long> response = new ApiResponse<>(true, "프로젝트 등록 완료", savedProject.getId());
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateProject(Long projectId, String token, ProjectAllRequest request) {

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

    //프로젝트 목록 리스트 가져오기
    public ResponseEntity<ApiResponse<List<ProjectMainResponse>>> getProjectList() {
        List<ProjectEntity> projects = projectRepository.findAll();

        // 모든 프로젝트에서 skillTags JSON 값 추출 후 DB에서 SkillTagEntity 조회
        List<String> skillNames = projects.stream()
                .flatMap(project -> project.getSkillTags().stream())
                .distinct()
                .collect(Collectors.toList());

        // 데이터베이스에서 스킬 이름으로 이미지 조회
        Map<String, String> skillImgMap = skillTagRepository.findByNameIn(skillNames).stream()
                .collect(Collectors.toMap(SkillTagEntity::getName, SkillTagEntity::getImg));

        List<ProjectMainResponse> projectList = projects.stream()
                .map(project -> {
                    UserEntity user = userRepository.findById(project.getAuthorId())
                            .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

                    // Service에서 조회한 이미지 정보를 `fromEntity()`에 전달
                    List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTags(), skillImgMap);

                    return ProjectMainResponse.fromEntity(project, user, skillResponses);
                })
                .collect(Collectors.toList());
        ApiResponse<List<ProjectMainResponse>> response = new ApiResponse<>(true, "프로젝트 목록 가져오기 성공", projectList);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<ProjectDetailResponse>> getProjectDetail(Long projectId, ProjectMainRequest projectMain) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if (projectMain == null){
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        ProjectDetailResponse projectDetail = ProjectDetailResponse.fromSummary(projectMain, project);
        ApiResponse<ProjectDetailResponse> response = new ApiResponse<>(true, "프로젝트 상세 정보 가져오기 성공", projectDetail);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<ProjectCountResponse>> getProjectCount() {

        long totalProjectCount = projectRepository.count();
        long ongoingProjectCount = projectRepository.countByIsDoneFalse();
        long endProjectCount = totalProjectCount - ongoingProjectCount;

        ProjectCountResponse responseData = new ProjectCountResponse(totalProjectCount, ongoingProjectCount, endProjectCount);

        ApiResponse<ProjectCountResponse> response = new ApiResponse<>(true, "프로젝트 개수 입니다.", responseData);
        return ResponseEntity.ok(response);
    }

    // SkillTag 변환 작업을 수행하는 메서드
    private List<SkillTagResponse> getSkillTagResponses(List<String> skillTagNames, Map<String, String> skillImgMap) {
        return skillTagNames.stream()
                .map(name -> new SkillTagResponse(name, skillImgMap.getOrDefault(name, "default-img-url")))
                .collect(Collectors.toList());
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