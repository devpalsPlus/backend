package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionTagProjectResponse {

    private Long id;
    private String name;
    private LocalDateTime createAt;

    public static PositionTagProjectResponse fromEntity(PositionTagEntity positionTag){
        return PositionTagProjectResponse.builder()
                .id(positionTag.getId())
                .name(positionTag.getName())
                .createAt(positionTag.getCreatedAt())
                .build();
    }
}
