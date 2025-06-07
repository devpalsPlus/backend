package hs.kr.backend.devpals.domain.Inquiry.entity;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
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

    @Builder.Default
    @Column(nullable = false)
    private Integer warning = 0;

    @Column(nullable = false)
    private Boolean state = false;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Builder 사용시 필드 초기화가 안돼서 사용
    private List<InquiryImageEntity> images = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "reportTargetId", referencedColumnName = "id")
    @SQLRestriction("report_filter = 'INQUIRY'")  // @Where 대신 @SQLRestriction 사용
    private List<ReportEntity> receivedReports = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void writeAnswer(String answer) {
        this.answer = answer;
        this.state = true;
    }

    public void updateAnswer(String answer) {
        this.answer = answer;
    }

    public static InquiryEntity from(InquiryResponse dto, UserEntity user) {
        return InquiryEntity.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .state(false)
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