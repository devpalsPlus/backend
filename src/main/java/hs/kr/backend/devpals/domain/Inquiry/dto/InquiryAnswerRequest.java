package hs.kr.backend.devpals.domain.Inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "문의 답변 DTO")
public class InquiryAnswerRequest {

    @Schema(description = "문의 ID", example = "12")
    private Long id;

    @Schema(description = "문의 답변 내용", example = "왜 오류가 나나요?")
    private String answer;
}
