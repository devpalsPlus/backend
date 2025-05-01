package hs.kr.backend.devpals.domain.user.entity.alarm;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static hs.kr.backend.devpals.global.constants.AlarmFilterConstants.*;

@Entity
@DiscriminatorValue(APPLICANT_CHECK)
@NoArgsConstructor
@Table(name = "ProjectAlarm") // 테이블 이름 지정
public class ProjectAlarmEntity extends AlarmEntity{
//공고에 지원자가 지원할 시 공고생성자가 수신받는 알림

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;


    public ProjectAlarmEntity(ProjectEntity project, UserEntity author, String content,ApplicantEntity applicant) {
        super(author,content, project.getId());
        this.project = project;
        this.applicant = applicant;
    }

    public Integer getAlarmFilterIntValue() {
        return APPLICANT_CHECK_INT_VALUE;
    }
}
