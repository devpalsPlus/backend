package hs.kr.backend.devpals.domain.report.dto;

import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.ReportFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReportSummaryResponse {
    private Long reportId;
    private Long userId;
    private String nickname;
    private String profileImg;
    private Integer warning;
    private ReportFilter category;
    private LocalDateTime reportedAt;
    private boolean isImposed;

    public static ReportSummaryResponse fromEntity(ReportEntity report, UserEntity user, boolean isImposed) {
        return ReportSummaryResponse.builder()
                .reportId(report.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .warning(user.getWarning())
                .category(report.getReportFilter())
                .reportedAt(report.getCreatedAt())
                .isImposed(isImposed)
                .build();
    }
}
