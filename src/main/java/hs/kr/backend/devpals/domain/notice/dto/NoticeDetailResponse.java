package hs.kr.backend.devpals.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailResponse {
    private Long id;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private PrevNextResponse prev;
    private PrevNextResponse next;

    public static NoticeDetailResponse fromEntity(NoticeEntity noticeEntity, PrevNextResponse prev, PrevNextResponse next) {
        return NoticeDetailResponse.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .createdAt(noticeEntity.getCreatedAt())
                .prev(prev)
                .next(next)
                .build();
    }
}
