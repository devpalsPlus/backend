package hs.kr.backend.devpals.domain.user.entity.alarm;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.AlarmFilter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ALARM_FILTER",
        discriminatorType = DiscriminatorType.STRING)

@Table(name = "Alarm")
public abstract class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;


    @Column(length = 255)
    private String content;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean enabled = false;

    @Column
    private Long routingId; //AlarmFilter마다


//    @Enumerated(EnumType.STRING)
//    private AlarmFilter alarmFilter;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AlarmEntity(UserEntity receiver, String content, Long routingId) {
        this.receiver = receiver;
        this.content = content;
        this.routingId = routingId;
    }

    public abstract Integer getAlarmFilterIntValue();
}
