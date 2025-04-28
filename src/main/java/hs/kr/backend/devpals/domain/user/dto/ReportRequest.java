package hs.kr.backend.devpals.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    private Long id;
    private Long reportTargetId;
    private Integer ReportFilter;
    private String reportReason;
    private String detail;
}
