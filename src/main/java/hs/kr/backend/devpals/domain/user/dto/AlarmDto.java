package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.alarm.AlarmEntity;
import hs.kr.backend.devpals.domain.user.entity.alarm.ApplicantAlarmEntity;
import hs.kr.backend.devpals.domain.user.entity.alarm.CommentAlarmEntity;
import hs.kr.backend.devpals.domain.user.entity.alarm.ProjectAlarmEntity;
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

    public static AlarmDto fromEntity(AlarmEntity entity) {
        if (entity instanceof CommentAlarmEntity) {
            return new CommentAlarmDto((CommentAlarmEntity) entity);
        }
        return AlarmDto.builder()
                .id(entity.getId())
                .routingId(entity.getRoutingId())
                .content(entity.getContent())
                .enabled(entity.isEnabled())
                .alarmFilterId(entity.getAlarmFilterIntValue())
                .createdAt(entity.getCreatedAt())
                .build();
    }


}
