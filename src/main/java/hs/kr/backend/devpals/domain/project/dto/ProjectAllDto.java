package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder

public class ProjectAllDto {

    private Long id;
    private String title;
    private String description;
    private int totalMember;
    private LocalDate startDate;
    private String estimatedPeriod;
    private MethodType methodType;
    private Boolean isBeginner;
    private Boolean isDone;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
    private Long authorId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> positionTagIds;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> skillTagIds;

    private List<PositionTagResponse> positions;
    private List<SkillTagResponse> skills;


    // response로 보내는 값
    public static ProjectAllDto fromEntity(ProjectEntity project, List<PositionTagResponse> positions, List<SkillTagResponse> skills) {
        return ProjectAllDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .totalMember(project.getTotalMember())
                .startDate(project.getStartDate())
                .estimatedPeriod(project.getEstimatedPeriod())
                .methodType(project.getMethod())
                .isBeginner(project.isBeginner())
                .isDone(project.isDone())
                .recruitmentStartDate(project.getRecruitmentStartDate())
                .recruitmentEndDate(project.getRecruitmentEndDate())
                .authorId(project.getAuthorId())
                .positions(positions)
                .skills(skills)
                .build();
    }
}

