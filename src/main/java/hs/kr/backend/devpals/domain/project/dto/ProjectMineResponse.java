package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectMineResponse {
    private String title;
    private LocalDate recruitmentEndDate;
    private int totalMember;
    private List<SkillTagResponse> skills;

    public static ProjectMineResponse fromEntity(ProjectEntity project, List<SkillTagResponse> skills) {
        return new ProjectMineResponse(
                project.getTitle(),
                project.getRecruitmentEndDate(),
                project.getTotalMember(),
                skills
        );
    }
}
