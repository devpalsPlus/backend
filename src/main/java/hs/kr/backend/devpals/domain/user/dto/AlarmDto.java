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
public class AlarmDto {
    private Long id;
    private Long routingId;
    private String content;
    private boolean enabled;
    private Integer alarmFilterId;
    private LocalDateTime createdAt;

    public static AlarmDto fromEntity(AlramEntity entity) {
        return AlarmDto.builder()
                .id(entity.getId())
                .routingId(entity.getProject().getId())
                .content(entity.getContent())
                .enabled(entity.isEnabled())
                .alarmFilterId(entity.getAlramFilter().getValue())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
