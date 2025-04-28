package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.project.entity.ReportEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private Long reportTargetId;
    private Integer ReportFilter;
    private String reportReason;
    private String detail;

    public static ReportResponse of(ReportEntity report) {
        return new ReportResponse(report.getId(), report.getReportTargetId(), report.getReportFilter().getValue(), report.getReportReason(), report.getDetail());

    }
}
