package hs.kr.backend.devpals.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class AdminVisitStatsResponse {
    private Map<String, Long> dailyVisits;
    private Map<String, Long> weeklyVisits;
    private Map<String, Long> monthlyVisits;

    public static AdminVisitStatsResponse from(
            Map<String, Long> daily,
            Map<String, Long> weekly,
            Map<String, Long> monthly
    ) {
        return AdminVisitStatsResponse.builder()
                .dailyVisits(daily)
                .weeklyVisits(weekly)
                .monthlyVisits(monthly)
                .build();
    }
}
