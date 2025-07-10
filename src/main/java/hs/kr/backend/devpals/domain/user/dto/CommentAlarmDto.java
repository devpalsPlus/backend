package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.alarm.CommentAlarmEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static hs.kr.backend.devpals.global.common.enums.ReplierFilter.COMMENT;
import static hs.kr.backend.devpals.global.common.enums.ReplierFilter.RECOMMENT;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentAlarmDto extends AlarmDto{

    private Integer replier;
    private Long reCommentUserId;

    public CommentAlarmDto(CommentAlarmEntity entity) {
        super(entity.getId(), entity.getRoutingId(), entity.getContent(), entity.isEnabled(), entity.getAlarmFilterIntValue(), entity.getCreatedAt());
        this.replier = entity.getReplier() ? RECOMMENT.getValue() : COMMENT.getValue();
        this.reCommentUserId = getReCommentUserIdIfNotNull(entity);
    }

    //대댓글이 존재하면 유저id 반환 없으면 0 반환
    private Long getReCommentUserIdIfNotNull(CommentAlarmEntity entity) {
        if(entity.getRecomment() != null && entity.getRecomment().getUser() != null) return entity.getRecomment().getUser().getId();
        else return 0L;
    }
}
