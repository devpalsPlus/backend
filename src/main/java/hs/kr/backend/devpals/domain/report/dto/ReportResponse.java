package hs.kr.backend.devpals.domain.report.dto;

import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private Long reportTargetId;
    private Integer reportFilter;
    private List<String> reason;
    private String detail;

    public static ReportResponse of(ReportEntity report, List<String> reason) {
        return new ReportResponse(report.getId(), report.getReportTargetId(), report.getReportFilter().getValue(), reason, report.getDetail());

    }
}
