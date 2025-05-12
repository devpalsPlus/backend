package hs.kr.backend.devpals.domain.evaluation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 참여자 응답 DTO")
public class EvaluationMemberResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "유저 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @Schema(description = "닉네임", example = "김철수")
    private String nickname;

    @Schema(description = "평가 완료 여부", example = "true")
    private boolean isEvaluated;

    public static EvaluationMemberResponse of(Long userId, String nickname, boolean isEvaluated) {
        return EvaluationMemberResponse.builder()
                .userId(userId)
                .nickname(nickname)
                .isEvaluated(isEvaluated)
                .build();
    }
}
