package hs.kr.backend.devpals.domain.banner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import hs.kr.backend.devpals.domain.banner.entity.BannerEntity;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerRequest {

    private boolean visible;
    private boolean always;

    @Schema(description = "시작 날짜", example = "2025-07-06 15:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "종료 날짜", example = "2025-07-06 20:00:00")
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