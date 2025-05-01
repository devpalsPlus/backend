package hs.kr.backend.devpals.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "내 댓글 Response")
public class MyCommentResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "댓글 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "댓글 내용", example = "이 프로젝트 정말 기대돼요!")
    private String content;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "작성 날짜", example = "2025-04-29T13:24:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "프로젝트 ID", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Long projectId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "프로젝트 제목", example = "AI 기반 추천 시스템 개발", accessMode = Schema.AccessMode.READ_ONLY)
    private String projectTitle;

    public static MyCommentResponse fromEntity(CommentEntity comment) {
        return MyCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .projectId(comment.getProject().getId())
                .projectTitle(comment.getProject().getTitle())
                .build();
    }
}
