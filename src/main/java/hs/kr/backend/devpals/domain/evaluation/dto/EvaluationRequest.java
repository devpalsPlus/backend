package hs.kr.backend.devpals.domain.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 제출 요청 DTO")
public class EvaluationRequest {

    @Schema(description = "프로젝트 ID", example = "1234")
    private Long projectId;

    @Schema(description = "평가 대상 유저 ID", example = "17")
    private Long evaluateeId;

    @Schema(description = "점수 배열", example = "[5, 4, 3, 3, 2, 5]")
    private List<Integer> scores;
}
