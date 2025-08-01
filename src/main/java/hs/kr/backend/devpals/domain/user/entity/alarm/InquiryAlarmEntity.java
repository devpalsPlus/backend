package hs.kr.backend.devpals.domain.user.entity.alarm;

import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static hs.kr.backend.devpals.global.constants.AlarmFilterConstants.INQUIRY;
import static hs.kr.backend.devpals.global.constants.AlarmFilterConstants.INQUIRY_INT_VALUE;

@Entity
@DiscriminatorValue(INQUIRY)
@NoArgsConstructor
@Getter
@Table(name = "InquiryAlarm")
public class InquiryAlarmEntity extends AlarmEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private InquiryEntity inquiry;

    public InquiryAlarmEntity(UserEntity receiver, String content, InquiryEntity inquiry) {
        super(receiver, content, inquiry.getId());
        this.inquiry = inquiry;
    }

    @Override
    public Integer getAlarmFilterIntValue() {
        return INQUIRY_INT_VALUE;
    }
}
