package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.AlramEntity;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlramDTO {
    private Long id;
    private Long project_id;
    private Long user_id;
    private String content;
    private boolean enabled;
    private AlramFilter alramFilter;
    private LocalDateTime createdAt;

    public static AlramDTO fromEntity(AlramEntity entity) {
        return AlramDTO.builder()
                .id(entity.getId())
                .project_id(entity.getProject().getId())
                .user_id(entity.getUser().getId())
                .content(entity.getContent())
                .enabled(entity.isEnabled())
                .alramFilter(entity.getAlramFilter())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
