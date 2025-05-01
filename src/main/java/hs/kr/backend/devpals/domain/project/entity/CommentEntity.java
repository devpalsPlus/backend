package hs.kr.backend.devpals.domain.project.entity;

import hs.kr.backend.devpals.domain.project.dto.CommentDTO;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Comment")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "projectId", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 255)
    private String content;

    @Builder.Default
    @Column(nullable = false)
    private Integer warning = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "reportTargetId", referencedColumnName = "id")
    @SQLRestriction("report_filter = 'COMMENT'")  // @Where 대신 @SQLRestriction 사용
    private List<ReportEntity> receivedReports = new ArrayList<>();

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public static CommentEntity from(CommentDTO dto, ProjectEntity project, UserEntity user) {
        return CommentEntity.builder()
                .project(project)
                .user(user)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    // warning 증가 메서드
    public void increaseWarning() {
        this.warning++;
    }
}
