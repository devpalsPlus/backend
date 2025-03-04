package hs.kr.backend.devpals.domain.project.entity;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ProjectPositionTag")
public class ProjectPositionTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "projectId", nullable = false)
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "positionTagId", nullable = false)
    private PositionTagEntity positionTag;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
