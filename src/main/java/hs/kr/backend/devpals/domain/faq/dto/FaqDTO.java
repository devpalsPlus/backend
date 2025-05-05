package hs.kr.backend.devpals.domain.faq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.faq.entity.FaqEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "FAQ DTO")
public class FaqDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "FAQ ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "FAQ 제목", example = "서비스 사용 관련")
    private String title;

    @Schema(description = "FAQ 내용", example = "서비스 이용하실 때 주의할 점")
    private String content;

    public static FaqDTO fromEntity(FaqEntity entity) {
        return FaqDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
    }
}
