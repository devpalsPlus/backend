package hs.kr.backend.devpals.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PrevNextResponse {
    private Long id;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static PrevNextResponse fromEntity(NoticeEntity noticeEntity) {
        return PrevNextResponse.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .createdAt(noticeEntity.getCreatedAt())
                .build();
    }
}
