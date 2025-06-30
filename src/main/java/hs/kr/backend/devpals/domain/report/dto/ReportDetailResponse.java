package hs.kr.backend.devpals.domain.report.dto;

import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ReportDetailResponse {

    private Long reportId;
    private ReportUserResponse reporter;
    private ReportUserResponse reportedUser;
    private LocalDateTime reportedAt;
    private String reason;
    private List<Long> category;
    private String location;
    private Long locationId;
    private boolean isImposed;

    public static ReportDetailResponse fromEntity(
            ReportEntity report,
            UserEntity reporterUser,
            UserEntity reportedUser
    ) {
        String location = switch (report.getReportFilter()) {
            case PROJECT  -> "공고 상세 페이지";
            case COMMENT  -> "댓글";
            case RECOMMENT-> "대댓글";
            case USER     -> "유저 프로필";
            default       -> "알 수 없음";
        };

        return ReportDetailResponse.builder()
                .reportId(report.getId())
                .reporter(ReportUserResponse.fromEntity(reporterUser))
                .reportedUser(ReportUserResponse.fromEntity(reportedUser))
                .reportedAt(report.getCreatedAt())
                .reason(report.getDetail())
                .category(report.getReportTagIds())
                .location(location)
                .locationId(report.getReportTargetId())
                .isImposed(report.isImposed())
                .build();
    }
}
