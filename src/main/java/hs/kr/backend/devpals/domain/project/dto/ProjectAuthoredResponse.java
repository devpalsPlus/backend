package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.MethodTypeEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProjectAuthoredResponse {

    private Long id;
    private String title;
    private String description;
    private int totalMember;
    private LocalDate startDate;
    private String estimatedPeriod;
    private Long authorId;
    private Integer views;
    private Boolean isBeginner;
    private Boolean isDone;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private MethodTypeResponse methodType;
    private List<PositionTagResponse> positions;
    private List<SkillTagResponse> skills;
    private Boolean canEvaluate;

    // Entity -> DTO 변환 메서드
    public static ProjectAuthoredResponse fromEntity(ProjectEntity project, List<PositionTagEntity> positionTag, List<SkillTagEntity> skillTag,MethodTypeEntity methodType) {
        boolean canEvaluate = LocalDate.now().isAfter(project.getStartDate().plusMonths(1));

        return ProjectAuthoredResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .totalMember(project.getTotalMember())
                .startDate(project.getStartDate())
                .estimatedPeriod(project.getEstimatedPeriod())
                .authorId(project.getUserId())
                .views(project.getViews())
                .isBeginner(project.isBeginner())
                .isDone(project.isDone())
                .recruitmentStartDate(project.getRecruitmentStartDate())
                .recruitmentEndDate(project.getRecruitmentEndDate())
                .createAt(project.getCreatedAt())
                .updateAt(project.getUpdatedAt())
                .methodType(
                        MethodTypeResponse.fromEntity(methodType)
                     )
                .positions(
                        positionTag.stream()
                                .map(PositionTagResponse::fromEntity) // 리스트 변환
                                .collect(Collectors.toList())
                )
                .skills(
                        skillTag.stream()
                                .map(SkillTagResponse::fromEntity) // 리스트 변환
                                .collect(Collectors.toList())
                )
                .canEvaluate(canEvaluate)
                .build();
    }
}

