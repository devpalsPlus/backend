package hs.kr.backend.devpals.domain.user.entity.alarm;

import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.entity.RecommentEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static hs.kr.backend.devpals.domain.user.entity.alarm.constants.AlarmFilterConstants.*;

@Entity
@DiscriminatorValue(COMMENT_AND_REPLY)
@NoArgsConstructor
@Getter
@Table(name = "CommentAlarm") // 테이블 이름 지정
public class CommentAlarmEntity extends AlarmEntity {
// 댓글 알람 -> 공고에 댓글을 달았을 시, 댓글에 답변을 달았을 시 전달되는 알람

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "recomment_id")
    private RecommentEntity recomment;

    @Column
    private Boolean replier;

    //공고에 댓글 달았을 시 공고 게시자에게 알람 전송
    public CommentAlarmEntity(CommentEntity comment, String content, ProjectEntity project, UserEntity projectAuthor) {
        super(projectAuthor,content,project.getId());
        this.comment = comment;
        this.project = project;
        this.replier = false;
    }

    public CommentAlarmEntity(CommentEntity comment, String content, ProjectEntity project, RecommentEntity recomment,UserEntity receiver) {
        super(receiver,content,project.getId());
        this.comment = comment;
        this.project = project;
        this.recomment = recomment;
        this.replier = true;
    }

    public Integer getAlarmFilterIntValue() {
        return COMMENT_AND_REPLY_INT_VALUE;
    }
}
