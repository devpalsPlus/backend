package hs.kr.backend.devpals.domain.project.entity;

import hs.kr.backend.devpals.domain.project.dto.RecommentDTO;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Recomment")
public class RecommentEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "commentId", nullable = false)
    private CommentEntity comment;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private Integer warning = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "reportTargetId", referencedColumnName = "id")
    @SQLRestriction("report_filter = 'RECOMMENT'")  // @Where 대신 @SQLRestriction 사용
    private List<ReportEntity> receivedReports = new ArrayList<>();

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public static RecommentEntity from(RecommentDTO dto, ProjectEntity project, UserEntity user, CommentEntity comment) {
        return RecommentEntity.builder()
                .project(project)
                .user(user)
                .comment(comment)
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
