package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectAuthoredResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMyResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserProjectOverviewResponse {
    private List<ProjectAuthoredResponse> authoredProjects; // 본인이 작성한
    private List<ProjectMyResponse> ownedProjects;          // 상대방이 작성한
    private List<ProjectMyResponse> joinedProjects;         // 상대방이 참여한
    private List<ProjectApplyResponse> appliedProjects;     // 본인이 지원한
    private List<ProjectMyResponse> myJoinedProjects;       // 본인이 참여한 ← 추가됨

    public static AdminUserProjectOverviewResponse of(
            List<ProjectAuthoredResponse> authored,
            List<ProjectMyResponse> owned,
            List<ProjectMyResponse> joined,
            List<ProjectApplyResponse> applied,
            List<ProjectMyResponse> myJoined
    ) {
        return AdminUserProjectOverviewResponse.builder()
                .authoredProjects(authored)
                .ownedProjects(owned)
                .joinedProjects(joined)
                .appliedProjects(applied)
                .myJoinedProjects(myJoined)
                .build();
    }
}