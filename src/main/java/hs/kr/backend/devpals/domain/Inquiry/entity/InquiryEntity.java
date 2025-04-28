package hs.kr.backend.devpals.domain.Inquiry.entity;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryDto;
import hs.kr.backend.devpals.domain.project.entity.ReportEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Inquiry")
public class InquiryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, length = 255)
    private String category;

    @Column(nullable = false)
    private Integer warning = 0;

    @Column(nullable = false)
    private Boolean isAnswered = false;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Builder 사용시 필드 초기화가 안돼서 사용
    private List<InquiryImageEntity> images = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "reportTargetId", referencedColumnName = "id")
    @SQLRestriction("report_filter = 'USER'")  // @Where 대신 @SQLRestriction 사용
    private List<ReportEntity> receivedReports = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 업데이트용 메서드 추가 가능
    public void update(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public static InquiryEntity from(InquiryDto dto, UserEntity user) {
        return InquiryEntity.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .isAnswered(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /*
    public void markAsAnswered() {
        this.isAnswered = true;
        this.updatedAt = LocalDateTime.now();
    }
     */
    // warning 증가 메서드
    public void increaseWarning() {
        this.warning++;
    }
}