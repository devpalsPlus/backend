package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectMyResponse {
    private Long id;
    private String title;
    private LocalDate recruitmentEndDate;
    private int totalMember;
    private Boolean isBeginner;
    private Boolean isDone;
    private List<SkillTagResponse> skills;
    private Boolean canEvaluate;

    public static ProjectMyResponse fromEntity(ProjectEntity project, List<SkillTagResponse> skills) {
        boolean canEvaluate = LocalDate.now().isAfter(project.getStartDate().plusMonths(1));

        return new ProjectMyResponse(
                project.getId(),
                project.getTitle(),
                project.getRecruitmentEndDate(),
                project.getTotalMember(),
                project.isBeginner(),
                project.isDone(),
                skills,
                canEvaluate
        );
    }
}
