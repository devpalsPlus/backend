package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectCloseResponse {
    private Long id;
    private String title;
    private String description;
    private Integer totalMember;
    private LocalDate startDate;
    private String estimatedPeriod;
    private MethodType methodType;
    private Long authorId;
    private Integer views;
    private Boolean isBeginner;
    private Boolean isDone;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static ProjectCloseResponse fromEntity(ProjectEntity project){
        return ProjectCloseResponse.builder()
                .title(project.getTitle())
                .description(project.getDescription())
                .totalMember(project.getTotalMember())
                .startDate(project.getStartDate())
                .estimatedPeriod(project.getEstimatedPeriod())
                .methodType(project.getMethod())
                .authorId(project.getAuthorId())
                .views(project.getViews())
                .isBeginner(project.isBeginner())
                .isDone(project.isDone())
                .recruitmentStartDate(project.getRecruitmentStartDate())
                .recruitmentEndDate(project.getRecruitmentEndDate())
                .createdAt(project.getCreatedAt())
                .updateAt(project.getUpdatedAt())
                .build();
    }
}
