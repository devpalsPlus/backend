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
@Schema(name = "BannerRequest", description = "배너 생성/수정 요청 DTO")
public class BannerRequest {

    @Schema(description = "배너 노출 여부", example = "true")
    private boolean visible;

    @Schema(description = "항상 노출 여부(true면 startDate/endDate는 무시)", example = "false")
    private boolean always;

    @Schema(description = "노출 시작일시(always=false일 때 사용)", example = "2026-01-03 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "노출 종료일시(always=false일 때 사용)", example = "2026-01-10 23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    public BannerEntity toEntity() {
        return BannerEntity.builder()
                .imageUrl("")
                .visible(visible)
                .always(always)
                .startDate(always ? null : startDate)
                .endDate(always ? null : endDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
