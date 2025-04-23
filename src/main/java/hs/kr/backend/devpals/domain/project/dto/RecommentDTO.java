package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.project.entity.RecommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "대댓글 DTO")
public class RecommentDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "대댓글 ID", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "대댓글 내용", example = "이 프로젝트 정말 기대돼요!")
    private String content;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "대댓글 작성자 정보", accessMode = Schema.AccessMode.READ_ONLY)
    private ProjectUserResponse user;

    public static RecommentDTO fromEntity(RecommentEntity recomment) {
        return RecommentDTO.builder()
                .id(recomment.getId())
                .content(recomment.getContent())
                .user(ProjectUserResponse.fromEntity(recomment.getUser()))
                .build();
    }
}
