package hs.kr.backend.devpals.domain.user.entity.alarm;

import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.project.entity.*;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static hs.kr.backend.devpals.global.constants.AlarmFilterConstants.*;

@Entity
@DiscriminatorValue(REPORT)
@NoArgsConstructor
@Table(name = "ReportAlarm") // 테이블 이름 지정
public class ReportAlarmEntity extends AlarmEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    public ReportAlarmEntity(UserEntity user, String content, ReportEntity report) {
        //User의 경우 Routing id가 애매해서 일단 해당 유저ID로 고정
        super(user,content, user.getId());
        this.report = report;
    }

    public ReportAlarmEntity(ProjectEntity project,UserEntity receiver, String content, ReportEntity report) {
        super(receiver,content, project.getId());
        this.report = report;
    }

    public ReportAlarmEntity(CommentEntity comment, String content, ReportEntity report) {
        //comment의 경우 ProjectId를 Routing할 수 있게 설정
        super(comment.getUser(),content, comment.getProject().getId());

        this.report = report;
    }
    public ReportAlarmEntity(RecommentEntity comment, String content, ReportEntity report) {
        //Recomment의 경우도 동일하게 ProjectId를 Routing할 수 있게 설정
        super(comment.getUser(),content, comment.getProject().getId());

        this.report = report;
    }
    public ReportAlarmEntity(InquiryEntity inquiry, String content, ReportEntity report) {
        super(inquiry.getUser(),content, inquiry.getId());

        this.report = report;
    }

    public Integer getAlarmFilterIntValue() {
        return REPORT_INT_VALUE;
    }
}
