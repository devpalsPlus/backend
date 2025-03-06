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
public class ProjectDetailResponse {
    private String title;
    private String description;
    private int totalMember;
    private LocalDate startDate;
    private String estimatedPeriod;
    private MethodType methodType;
    private boolean isBeginner;
    private boolean isDone;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
    private String authorNickname;
    private int views;
    private List<SkillTagResponse> skills;
    private List<String> positions;

    // 목록에서 본 정보를 포함하여 생성하는 DTO
    public static ProjectDetailResponse fromSummary(
            ProjectMainRequest projectMain, ProjectEntity project) {
        return new ProjectDetailResponse(
                projectMain.getTitle(),
                project.getDescription(),
                project.getTotalMember(),
                project.getStartDate(),
                project.getEstimatedPeriod(),
                project.getMethod(),
                project.isBeginner(),
                project.isDone(),
                project.getRecruitmentStartDate(),
                project.getRecruitmentEndDate(),
                projectMain.getAuthorNickname(),
                projectMain.getViews(),
                projectMain.getSkills(),
                projectMain.getPositions()
        );
    }
}

