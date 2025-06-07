package hs.kr.backend.devpals.domain.Inquiry.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryPreviewResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "문의 제목", example = "서비스 사용 관련 문의")
    private String title;

    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String nickname;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 상태", example = "대기중", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean state;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 작성 날짜", example = "2025-06-01", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public static InquiryPreviewResponse fromEntity(InquiryEntity entity) {
        return InquiryPreviewResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .nickname(entity.getUser().getNickname())
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

