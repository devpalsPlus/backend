package hs.kr.backend.devpals.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectParticipationResponse {
    private List<ProjectMyResponse> acceptedProjects;
    private List<ProjectApplyResponse> ownProjects;

    public static ProjectParticipationResponse from(List<ProjectMyResponse> accepted, List<ProjectApplyResponse> own) {
        return new ProjectParticipationResponse(accepted, own);
    }
}

