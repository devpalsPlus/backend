package hs.kr.backend.devpals.domain.report.entity;

import hs.kr.backend.devpals.domain.report.dto.ReportRequest;
import hs.kr.backend.devpals.domain.user.convert.LongListConverter;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.ReportFilter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "reportFilter", nullable = false)
    private ReportFilter reportFilter;

    @Convert(converter = LongListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Long> reportTagIds = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean imposed = false;

    public ReportEntity(ReportRequest request, List<Long> reportTagIds, UserEntity reporter) {
        this.reporter = reporter;
        this.reportFilter = ReportFilter.fromValue(request.getReportFilter());
        this.reportTagIds = (reportTagIds == null) ? new ArrayList<>() : new ArrayList<>(reportTagIds);
        this.detail = request.getDetail();
        this.reportTargetId = request.getReportTargetId();
    }

    public void impose() {
        this.imposed = true;
        this.updatedAt = LocalDateTime.now();
    }
}
