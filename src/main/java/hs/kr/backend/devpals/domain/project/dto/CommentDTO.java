package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "댓글 DTO")
public class CommentDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "댓글 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "댓글 내용", example = "이 프로젝트 정말 기대돼요!")
    private String content;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "댓글 작성자 정보", accessMode = Schema.AccessMode.READ_ONLY)
    private ProjectUserResponse user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "대댓글 수", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private int commentCount;

    public static CommentDTO fromEntity(CommentEntity comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(ProjectUserResponse.fromEntity(comment.getUser()))
                .commentCount(0)
                .build();
    }
}
