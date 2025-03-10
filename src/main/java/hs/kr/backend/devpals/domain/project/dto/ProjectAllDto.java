package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
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
    private String authorNickname;
    private String authorImage;
    private List<String> positions;
    private List<SkillTagResponse> skills;

    public static ProjectAllDto fromEntity(ProjectEntity project, String authorNickname, String authorImage, List<SkillTagResponse> skillImgMap) {
        return new ProjectAllDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getTotalMember(),
                project.getStartDate(),
                project.getEstimatedPeriod(),
                project.getMethod(),
                project.isBeginner(),
                project.isDone(),
                project.getRecruitmentStartDate(),
                project.getRecruitmentEndDate(),
                project.getAuthorId(),
                authorNickname,
                authorImage,
                project.getPositionTagsAsList(),
                skillImgMap
        );
    }
}

