package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProjectPostRequest {

    private Long id;
    private String title;
    private String description;
    private int totalMember;
    private LocalDate startDate;
    private String estimatedPeriod;
    private Boolean isBeginner;
    private Boolean isDone;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;

    private Long methodTypeId;
    private List<Long> positionTagIds;
    private List<Long> skillTagIds;

    public static ProjectUpdateRequest fromEntity(ProjectEntity project,
                                                  List<Long> positionTagIds,
                                                  List<Long> skillTagIds,
                                                  Long methodTypeId) {
        return ProjectUpdateRequest.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .totalMember(project.getTotalMember())
                .startDate(project.getStartDate())
                .estimatedPeriod(project.getEstimatedPeriod())
                .isBeginner(project.isBeginner())
                .isDone(project.isDone())
                .recruitmentStartDate(project.getRecruitmentStartDate())
                .recruitmentEndDate(project.getRecruitmentEndDate())
                .methodTypeId(methodTypeId)
                .positionTagIds(positionTagIds)
                .skillTagIds(skillTagIds)
                .build();
    }
}
