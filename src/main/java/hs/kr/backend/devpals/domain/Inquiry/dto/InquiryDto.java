package hs.kr.backend.devpals.domain.Inquiry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryImageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "문의 작성 요청 DTO")
public class InquiryDto {

    @Schema(description = "문의 제목", example = "서비스 사용 관련 문의")
    private String title;

    @Schema(description = "문의 내용", example = "서비스를 이용하다가 발생한 오류에 대해 문의드립니다.")
    private String content;

    @Schema(description = "문의 카테고리", example = "오류/버그 신고")
    private String category;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "등록된 이미지 URL 목록",
            example = "[\"https://devpal.s3.ap-northeast-2.amazonaws.com/devpals_inquiry1-1-1.png\", \"https://...\"]",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<String> imageUrls;

    public static InquiryDto fromEntity(InquiryEntity inquiry) {
        List<String> imageUrls = inquiry.getImages()
                .stream()
                .map(InquiryImageEntity::getImageUrl)
                .toList();

        return InquiryDto.builder()
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .category(inquiry.getCategory())
                .imageUrls(imageUrls)
                .build();
    }
}
