package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectRequest;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public ResponseEntity<ApiResponse<String>> projectSignup(ProjectRequest request) {

        // 🔹 positionTagIds를 태그 이름으로 변환
        List<String> positionTagNames = request.getPositionTagIds().stream()
                .map(id -> positionTagRepository.findById(id)
                        .map(PositionTagEntity::getName)
                        .orElseThrow(() -> new CustomException(ErrorException.POSITION_NOT_FOUND)))
                .collect(Collectors.toList());

        // 🔹 skillTagIds를 태그 이름으로 변환
        List<String> skillTagNames = request.getSkillTagIds().stream()
                .map(id -> skillTagRepository.findById(id)
                        .map(SkillTagEntity::getName)
                        .orElseThrow(() -> new CustomException(ErrorException.SKILL_NOT_FOUND)))
                .collect(Collectors.toList());

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

        project.setPositionTags(positionTagNames);
        project.setSkillTags(skillTagNames);

        projectRepository.save(project);

        return ResponseEntity.ok(new ApiResponse<>(true, "프로젝트 등록 완료", null));
    }
}