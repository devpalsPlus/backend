package hs.kr.backend.devpals.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "공지사항 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "공지사항 제목", example = "Devplas 입니다.")
    private String title;

    @Schema(description = "공지사항 내용", example = "저희 웹은 사이드 프로젝트 공고 모집...")
    private String content;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "공지사항 작성 날짜", example = "2025.05.08 15:49:38", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "공지사항 조회수", example = "112", accessMode = Schema.AccessMode.READ_ONLY)
    private int viewCount;

    private PrevNextResponse prev;
    private PrevNextResponse next;

    public static NoticeDetailResponse fromEntity(NoticeEntity noticeEntity, PrevNextResponse prev, PrevNextResponse next) {
        return NoticeDetailResponse.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .createdAt(noticeEntity.getCreatedAt())
                .viewCount(noticeEntity.getViewCount())
                .prev(prev)
                .next(next)
                .build();
    }
}
