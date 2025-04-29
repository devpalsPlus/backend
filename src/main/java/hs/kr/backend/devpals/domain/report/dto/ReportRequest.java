package hs.kr.backend.devpals.domain.report.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    private Long reportTargetId;
    private Integer reportFilter;
    private String reportReason;
    private String detail;
}
