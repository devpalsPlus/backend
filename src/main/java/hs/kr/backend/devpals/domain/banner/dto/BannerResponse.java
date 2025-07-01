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
public class BannerResponse {

    private Long id;
    private String imageUrl;
    private boolean isVisible;
    private boolean isAlways;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
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
