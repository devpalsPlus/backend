package hs.kr.backend.devpals.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CommentDTO {
    private Long id;
    private Long project_id;
    private Long user_id;
    private String content;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
