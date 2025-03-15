package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectMainResponse {
    private Long projectId;
    private String title;
    private boolean isDone;
    private LocalDate recruitmentEndDate;
    private List<SkillTagResponse> skills;
    private List<PositionTagResponse> positions;
    private String authorNickname;
    private int views;

    public static ProjectMainResponse fromEntity(ProjectEntity project, UserEntity user, List<SkillTagResponse> skills,
                                                 List<PositionTagResponse> positions) {
        return new ProjectMainResponse(
                project.getId(),
                project.getTitle(),
                project.isDone(),
                project.getRecruitmentEndDate(),
                skills,
                positions,
                user.getNickname(),
                project.getViews()
        );
    }
}