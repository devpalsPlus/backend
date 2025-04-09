package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int views;

    private Boolean isBeginner;
    private Boolean isDone;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ProjectUserResponse user;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> positionTagIds;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> skillTagIds;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long methodTypeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MethodTypeResponse methodType;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<PositionTagResponse> positions;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<SkillTagResponse> skills;


    // response로 보내는 값
    public static ProjectAllDto fromEntity(ProjectEntity project, List<PositionTagResponse> positions,
                                           List<SkillTagResponse> skills,MethodTypeResponse methodType, ProjectUserResponse user) {
        return ProjectAllDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .totalMember(project.getTotalMember())
                .startDate(project.getStartDate())
                .estimatedPeriod(project.getEstimatedPeriod())
                .views(project.getViews())
                .isBeginner(project.isBeginner())
                .isDone(project.isDone())
                .recruitmentStartDate(project.getRecruitmentStartDate())
                .recruitmentEndDate(project.getRecruitmentEndDate())
                .user(user)
                .methodType(methodType)
                .positions(positions)
                .skills(skills)
                .build();
    }
}

