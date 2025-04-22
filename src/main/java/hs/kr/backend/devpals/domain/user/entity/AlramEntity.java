package hs.kr.backend.devpals.domain.user.entity;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Alram")
public class AlramEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    @Column(length = 255)
    private String content;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean enabled;

    @Column
    private Long routingId; //AlarmFilter마다

    @Enumerated(EnumType.STRING)
    private AlramFilter alramFilter;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AlramEntity(ApplicantEntity applicant, String content, AlramFilter alramFilter,Long routingId) {
        this.project = applicant.getProject();
        this.user = applicant.getUser();
        this.content = content;
        this.routingId = routingId;
        this.enabled = false;
        this.alramFilter = alramFilter;
    }
    public AlramEntity(ProjectEntity project,UserEntity author, String content, AlramFilter alramFilter,Long routingId) {
        this.project = project;
        this.user = author;
        this.content = content;
        this.routingId = routingId;
        this.enabled = false;
        this.alramFilter = alramFilter;
    }
}
