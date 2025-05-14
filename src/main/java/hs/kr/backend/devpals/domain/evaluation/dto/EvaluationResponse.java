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
@Schema(description = "평가 참여자 조회 응답 DTO")
public class EvaluationResponse {

    @Schema(description = "프로젝트 이름", example = "AI 커뮤니티 플랫폼 개발")
    private String projectName;

    @Schema(description = "참여자 평가 상태 리스트")
    private List<EvaluationMemberResponse> userData;

    public static EvaluationResponse of(String projectName, List<EvaluationMemberResponse> userData) {
        return EvaluationResponse.builder()
                .projectName(projectName)
                .userData(userData)
                .build();
    }
}
