package hs.kr.backend.devpals.domain.user.entity.alarm;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static hs.kr.backend.devpals.global.constants.AlarmFilterConstants.APPLIED_PROJECTS;
import static hs.kr.backend.devpals.global.constants.AlarmFilterConstants.APPLIED_PROJECT_INT_VALUE;

@Entity
@DiscriminatorValue(APPLIED_PROJECTS)
@NoArgsConstructor
@Table(name = "ApplicantAlarm") // 테이블 이름 지정
public class ApplicantAlarmEntity extends AlarmEntity {
    //"지원한 프로젝트" 알람 (지원 결과)
    //공고 지원자가 지원한 프로젝트에 합격, 불합격 여부를 전달하는 알람
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;

    public ApplicantAlarmEntity(ApplicantEntity applicantEntity, String content, ProjectEntity project) {
        super(applicantEntity.getUser(),content,project.getId());
        this.project = project;
        this.applicant = applicantEntity;

    }

    public Integer getAlarmFilterIntValue() {
        return APPLIED_PROJECT_INT_VALUE;
    }
}
