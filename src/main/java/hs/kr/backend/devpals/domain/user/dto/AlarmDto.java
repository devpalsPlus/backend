package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.alarm.AlarmEntity;
import hs.kr.backend.devpals.domain.user.entity.alarm.CommentAlarmEntity;
import hs.kr.backend.devpals.domain.user.dto.CommentAlarmDto;
import hs.kr.backend.devpals.domain.user.entity.alarm.InquiryAlarmEntity;
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
        //TODO: 추후 타입에 따라 전달하는 데이터가 다를 수 있으므로 ApplicantAlarmDto, ProjectAlarmDto 작성 가능성여부 검토필요
        if (entity instanceof CommentAlarmEntity) {
            return new CommentAlarmDto((CommentAlarmEntity) entity);
        }

        if (entity instanceof InquiryAlarmEntity) {
            return new InquiryAlarmDto((InquiryAlarmEntity) entity);
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
