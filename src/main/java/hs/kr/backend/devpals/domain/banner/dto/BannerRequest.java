package hs.kr.backend.devpals.domain.banner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private boolean isVisible;
    private boolean isAlways;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    public BannerEntity toEntity() {
        return BannerEntity.builder()
                .imageUrl(null)
                .isVisible(isVisible)
                .isAlways(isAlways)
                .startDate(isAlways ? null : startDate)
                .endDate(isAlways ? null : endDate)
                .build();
    }
}