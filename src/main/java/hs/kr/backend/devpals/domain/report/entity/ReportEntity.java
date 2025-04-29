package hs.kr.backend.devpals.domain.report.entity;

import hs.kr.backend.devpals.domain.report.dto.ReportRequest;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.ReportFilter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Report")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reportTargetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporterId", nullable = false)
    private UserEntity reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportFilter reportFilter;

    @Column(nullable = false, length = 255)
    private String reportReason;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();


    public ReportEntity(ReportRequest request, UserEntity reporter) {
        this.reporter = reporter;
        this.reportFilter = ReportFilter.fromValue(request.getReportFilter());
        this.reportReason = request.getReportReason();
        this.detail = request.getDetail();
        this.reportTargetId = request.getReportTargetId();
    }
}
