package hs.kr.backend.devpals.domain.project.entity;

import hs.kr.backend.devpals.domain.project.dto.ProjectAllDto;
import hs.kr.backend.devpals.domain.user.convert.LongListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Projects")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int totalMember;

    @Column(nullable = false)
    private LocalDate startDate;

    private String estimatedPeriod;

//    @Convert(converter = MethodTypeConverter.class)
//    @Column(nullable = false)
//    private MethodType method;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isBeginner;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDone;

    @Column(nullable = false)
    private LocalDate recruitmentStartDate;

    @Column(nullable = false)
    private LocalDate recruitmentEndDate;

    @Convert(converter = LongListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Long> positionTagIds;

    @Convert(converter = LongListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Long> skillTagIds;

    private Long methodTypeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int views;

    @Column(nullable = false)
    private Integer warning = 0;

    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "reportTargetId", referencedColumnName = "id")
    @SQLRestriction("report_filter = 'PROJECT'")  // @Where 대신 @SQLRestriction 사용
    private List<ReportEntity> receivedReports = new ArrayList<>();

    // Request 보내줘야 하는 값
    public static ProjectEntity fromRequest(ProjectAllDto request, Long userId) {
        return ProjectEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .totalMember(request.getTotalMember())
                .startDate(request.getStartDate())
                .estimatedPeriod(request.getEstimatedPeriod())
                .views(0)
                .userId(userId)
                .isBeginner(request.getIsBeginner() != null ? request.getIsBeginner() : false)
                .isDone(request.getIsDone() != null ? request.getIsDone() : false)
                .recruitmentStartDate(request.getRecruitmentStartDate())
                .recruitmentEndDate(request.getRecruitmentEndDate())
                .methodTypeId(request.getMethodTypeId())
                .positionTagIds(request.getPositionTagIds())
                .skillTagIds(request.getSkillTagIds())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateProject(ProjectAllDto request) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.totalMember = request.getTotalMember();
        this.startDate = request.getStartDate();
        this.estimatedPeriod = request.getEstimatedPeriod();
        this.isBeginner = request.getIsBeginner();
        this.isDone = request.getIsDone();
        this.recruitmentStartDate = request.getRecruitmentStartDate();
        this.recruitmentEndDate = request.getRecruitmentEndDate();
        this.methodTypeId = request.getMethodTypeId();
        this.positionTagIds = request.getPositionTagIds();
        this.skillTagIds = request.getSkillTagIds();
    }

    public List<Long> getSkillTagsAsList() {
        return skillTagIds != null ? skillTagIds : Collections.emptyList();
    }

    public void updateIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    // warning 증가 메서드
    public void increaseWarning() {
        this.warning++;
    }
}
