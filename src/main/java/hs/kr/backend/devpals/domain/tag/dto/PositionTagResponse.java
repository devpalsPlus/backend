package hs.kr.backend.devpals.domain.tag.dto;

import hs.kr.backend.devpals.domain.tag.entity.PositionTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PositionTagResponse {
    private Long id;
    private String name;

    public static PositionTagResponse fromEntity(PositionTagEntity position) {
        return new PositionTagResponse(position.getId(), position.getName());
    }
}
