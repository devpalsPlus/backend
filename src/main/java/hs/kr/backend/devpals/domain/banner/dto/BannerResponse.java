package hs.kr.backend.devpals.domain.banner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hs.kr.backend.devpals.domain.banner.entity.BannerEntity;
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
@Schema(name = "BannerResponse", description = "배너 응답 DTO")
public class BannerResponse {

    @Schema(description = "배너 ID", example = "1")
    private Long id;

    @Schema(description = "배너 이미지 URL", example = "https://cdn.example.com/banner/1.png")
    private String imageUrl;

    @Schema(description = "배너 노출 여부", example = "true")
    private boolean isVisible;

    @Schema(description = "항상 노출 여부", example = "false")
    private boolean isAlways;

    @Schema(description = "노출 시작일시", example = "2026-01-03 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "노출 종료일시", example = "2026-01-10 23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    public static BannerResponse from(BannerEntity banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .imageUrl(banner.getImageUrl())
                .isVisible(banner.isVisible())
                .isAlways(banner.isAlways())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .build();
    }
}
