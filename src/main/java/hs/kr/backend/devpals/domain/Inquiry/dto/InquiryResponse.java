package hs.kr.backend.devpals.domain.Inquiry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryImageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "문의 작성 요청 DTO")
public class InquiryResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "문의 제목", example = "서비스 사용 관련 문의")
    private String title;

    @Schema(description = "문의 내용", example = "서비스를 이용하다가 발생한 오류에 대해 문의드립니다.")
    private String content;

    @Schema(description = "문의 카테고리", example = "오류/버그 신고")
    private String category;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 상태", example = "대기중", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean state;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 답변", example = "재부팅 해보시길 바랍니다.", accessMode = Schema.AccessMode.READ_ONLY)
    private String answer;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "등록된 이미지 URL 목록",
            example = "[\"https://devpal.s3.ap-northeast-2.amazonaws.com/devpals_inquiry1-1-1.png\", \"https://...\"]",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<String> imageUrls;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 작성 날짜", example = "2025-06-01", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "문의 작성 유저 정보", example = "ID, Nickname, Img", accessMode = Schema.AccessMode.READ_ONLY)
    private InquiryWriterResponse user;

    public static InquiryResponse fromEntity(InquiryEntity inquiry) {
        List<String> imageUrls = inquiry.getImages()
                .stream()
                .map(InquiryImageEntity::getImageUrl)
                .toList();

        return InquiryResponse.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .category(inquiry.getCategory())
                .state(inquiry.getState())
                .answer(inquiry.getAnswer())
                .imageUrls(imageUrls)
                .createdAt(inquiry.getCreatedAt())
                .user(InquiryWriterResponse.fromEntity(inquiry.getUser()))
                .build();
    }
}
