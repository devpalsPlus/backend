package hs.kr.backend.devpals.domain.notice.entity;

import hs.kr.backend.devpals.domain.notice.dto.NoticeDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notice")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static NoticeEntity fromDTO(NoticeDTO noticeDto) {
        return NoticeEntity.builder()
                .id(noticeDto.getId())
                .title(noticeDto.getTitle())
                .content(noticeDto.getContent())
                .viewCount(noticeDto.getViewCount())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
