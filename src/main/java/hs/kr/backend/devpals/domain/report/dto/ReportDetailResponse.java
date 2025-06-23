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
public class ReportDetailResponse {
    private Long reportId;
    private Long userId;
    private String nickname;
    private String profileImg;
    private LocalDateTime reportedAt;
    private String reason;
    private ReportFilter category;
    private String location;
    private Long locationId;

    public static ReportDetailResponse fromEntity(ReportEntity report, UserEntity user) {
        String location = switch (report.getReportFilter()) {
            case PROJECT -> "공고 상세 페이지";
            case COMMENT -> "댓글";
            case RECOMMENT -> "대댓글";
            case USER -> "유저 프로필";
            default -> "알 수 없음";
        };

        return ReportDetailResponse.builder()
                .reportId(report.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .reportedAt(report.getCreatedAt())
                .reason(report.getDetail())
                .category(report.getReportFilter())
                .location(location)
                .locationId(report.getReportTargetId())
                .build();
    }

}
