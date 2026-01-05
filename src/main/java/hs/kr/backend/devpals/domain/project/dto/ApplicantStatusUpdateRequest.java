package hs.kr.backend.devpals.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "ApplicantStatusUpdateRequest", description = "지원자 상태 변경 요청 DTO")
public class ApplicantStatusUpdateRequest {

    @Schema(description = "상태 변경 대상 지원자(유저) ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long applicantUserId;

    @Schema(
            description = "변경할 지원 상태 (예: PENDING / ACCEPTED / REJECTED)",
            example = "ACCEPTED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;
}
