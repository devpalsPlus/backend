package hs.kr.backend.devpals.domain.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 제출 요청 DTO")
public class EvaluationRequest {

    @Schema(description = "프로젝트 ID", example = "1234")
    private Long projectId;

    @Schema(description = "평가 항목 목록")
    private List<EvaluationItem> evaluations;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "단일 평가 항목 DTO")
    public static class EvaluationItem {

        @Schema(description = "평가 대상 유저 ID", example = "17")
        private Long evaluateeId;

        @Schema(description = "평가 점수 배열", example = "[5, 2, 3, 2, 1, 4]")
        private List<Integer> scores;
    }
}
