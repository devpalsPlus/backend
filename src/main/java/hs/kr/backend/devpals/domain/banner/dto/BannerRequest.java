package hs.kr.backend.devpals.domain.banner.dto;

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

    private String imageUrl;
    private boolean isVisible;
    private boolean isAlways;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public BannerEntity toEntity() {
        return BannerEntity.builder()
                .imageUrl(imageUrl)
                .isVisible(isVisible)
                .isAlways(isAlways)
                .startDate(isAlways ? null : startDate)
                .endDate(isAlways ? null : endDate)
                .build();
    }
}