package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectRequest;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectPositionTagEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectSkillTagEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectPositionTagRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectSkillTagRepository;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectPositionTagRepository projectPositionTagRepository;
    private final ProjectSkillTagRepository projectSkillTagRepository;
    private final PositionTagRepository positionTagRepository;
    private final SkillTagRepository skillTagRepository;

    @Transactional
    public ResponseEntity<ApiResponse<String>> projectSignup(ProjectRequest request) {

        // 태그 조회 먼저 수행
        Set<PositionTagEntity> positionTags = request.getPositionTagIds().stream()
                .map(id -> positionTagRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ErrorException.POSITION_NOT_FOUND)))
                .collect(Collectors.toSet());

        Set<SkillTagEntity> skillTags = request.getSkillTagIds().stream()
                .map(id -> skillTagRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ErrorException.SKILL_NOT_FOUND)))
                .collect(Collectors.toSet());

        // 태그가 정상적으로 조회되면 프로젝트 저장
        ProjectEntity project = ProjectEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .totalMember(request.getTotalMember())
                .startDate(request.getStartDate())
                .estimatedPeriod(request.getEstimatedPeriod())
                .method(request.getMethodType())
                .authorId(request.getAuthorId())
                .isBeginner(request.getIsBeginner() != null ? request.getIsBeginner() : false)
                .isDone(request.getIsDone() != null ? request.getIsDone() : false)
                .recruitmentStartDate(request.getRecruitmentStartDate())
                .recruitmentEndDate(request.getRecruitmentEndDate())
                .views(0)
                .build();

        projectRepository.save(project);

        // 중간 테이블 데이터 저장
        Set<ProjectPositionTagEntity> projectPositionTags = positionTags.stream()
                .map(positionTag -> ProjectPositionTagEntity.builder()
                        .project(project)
                        .positionTag(positionTag)
                        .build())
                .collect(Collectors.toSet());
        projectPositionTagRepository.saveAll(projectPositionTags);

        Set<ProjectSkillTagEntity> projectSkillTags = skillTags.stream()
                .map(skillTag -> ProjectSkillTagEntity.builder()
                        .project(project)
                        .skillTag(skillTag)
                        .build())
                .collect(Collectors.toSet());
        projectSkillTagRepository.saveAll(projectSkillTags);

        ApiResponse<String> response = new ApiResponse<>(true, "프로젝트 등록 완료되었습니다.", null);
        return ResponseEntity.ok(response);
    }
}
