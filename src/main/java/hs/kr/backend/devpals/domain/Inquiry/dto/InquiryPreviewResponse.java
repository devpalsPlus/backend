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

    @Schema(description = "문의 ID", example = "12")
    private Long id;

    @Schema(description = "문의 제목", example = "서비스 사용 관련 문의")
    private String title;

    @Schema(description = "문의 상태", example = "대기중")
    private Boolean state;

    @Schema(description = "문의 카테고리", example = "서비스 이용 문제")
    private String category;

    @Schema(description = "첨부파일 존재 여부", example = "true")
    private Boolean hasFile;

    @Schema(description = "문의 작성 날짜", example = "2025-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @Schema(description = "문의 작성 유저 정보", example = "ID, Nickname, Img")
    private InquiryWriterResponse user;

    public static InquiryPreviewResponse fromEntity(InquiryEntity entity) {
        return InquiryPreviewResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .state(entity.getState())
                .category(entity.getCategory())
                .hasFile(!entity.getImages().isEmpty())
                .createdAt(entity.getCreatedAt())
                .user(InquiryWriterResponse.fromEntity(entity.getUser()))
                .build();
    }
}

