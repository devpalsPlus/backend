package hs.kr.backend.devpals.domain.tag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hs.kr.backend.devpals.domain.tag.entity.PositionTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PositionTagResponse {
    private Long id;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static PositionTagResponse fromEntity(PositionTagEntity position) {
        return new PositionTagResponse(position.getId(), position.getName(), position.getUpdatedAt());
    }
}
