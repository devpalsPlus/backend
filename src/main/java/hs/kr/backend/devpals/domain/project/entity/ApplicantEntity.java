package hs.kr.backend.devpals.domain.project.entity;

import hs.kr.backend.devpals.domain.project.convert.CareerConverter;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplyDTO;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Builder
@Getter
@Table(name = "Applicant")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private ProjectEntity project;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 255)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @Convert(converter = CareerConverter.class)
    @Column(columnDefinition = "JSON")
    private List<CareerDto> career;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicantStatus status = ApplicantStatus.WAITING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 지원 상태 업데이트 메서드
    public void updateStatus(ApplicantStatus newStatus) {
        this.status = newStatus;
    }

    public static ApplicantEntity createApplicant(UserEntity user, ProjectEntity project, ProjectApplyDTO request) {
        return ApplicantEntity.builder()
                .user(user)
                .project(project)
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .message(request.getMessage())
                .career(request.getCareer() != null ? request.getCareer() : Collections.emptyList())
                .status(ApplicantStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
