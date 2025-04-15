package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectApplyResponse {
    private Long id;
    private String title;
    private ApplicantStatus status;

    public static ProjectApplyResponse fromEntity(Long id, String title, ApplicantStatus status) {
        return new ProjectApplyResponse(id, title, status);
    }
}
