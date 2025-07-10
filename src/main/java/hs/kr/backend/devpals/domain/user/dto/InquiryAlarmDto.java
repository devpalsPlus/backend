package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.alarm.InquiryAlarmEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static hs.kr.backend.devpals.global.common.enums.ReplierFilter.INQUIRY;

@Getter
@NoArgsConstructor
public class InquiryAlarmDto extends AlarmDto {

    private Integer replier;

    public InquiryAlarmDto(InquiryAlarmEntity entity) {
        super(
                entity.getId(),
                entity.getRoutingId(),
                entity.getContent(),
                entity.isEnabled(),
                entity.getAlarmFilterIntValue(),
                entity.getCreatedAt()
        );
        this.replier = INQUIRY.getValue();
    }
}
