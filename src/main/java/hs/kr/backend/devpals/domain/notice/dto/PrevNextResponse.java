package hs.kr.backend.devpals.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PrevNextResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "공지사항 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "공지사항 제목", example = "Devplas 입니다.")
    private String title;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "공지사항 작성 날짜", example = "2025.05.08 15:49:38", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    public static PrevNextResponse fromEntity(NoticeEntity noticeEntity) {
        return PrevNextResponse.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .createdAt(noticeEntity.getCreatedAt())
                .build();
    }
}
