package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "ProjectApplyResponse", description = "프로젝트 지원 결과(내 지원 상태) 응답 DTO")
public class ProjectApplyResponse {

    @Schema(description = "프로젝트 ID", example = "7")
    private Long id;

    @Schema(description = "프로젝트 제목", example = "DevPals 팀원 모집합니다")
    private String title;

    @Schema(description = "지원 상태", example = "PENDING")
    private ApplicantStatus status;

    public static ProjectApplyResponse fromEntity(Long id, String title, ApplicantStatus status) {
        return new ProjectApplyResponse(id, title, status);
    }
}
