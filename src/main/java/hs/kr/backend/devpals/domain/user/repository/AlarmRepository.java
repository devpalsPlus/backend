package hs.kr.backend.devpals.domain.user.repository;

import hs.kr.backend.devpals.domain.user.entity.alarm.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
    @Query("SELECT a FROM AlarmEntity a WHERE a.receiver.id = :receiverId")
    List<AlarmEntity> findByReceiverId(@Param("receiverId") Long receiverId);

    @Query("SELECT a FROM AlarmEntity a WHERE a.receiver.id = :receiverId AND a.id = :alarmId")
    Optional<AlarmEntity> findByReceiverIdAndAlarmId(@Param("receiverId") Long receiverId, @Param("alarmId") Long alarmId);

    @Modifying
    @Query("DELETE FROM AlarmEntity a WHERE a.createdAt < :threshold AND TYPE(a) <> ApplicantAlarmEntity")
    void deleteAllOlderThanExceptApplied(@Param("threshold") LocalDateTime threshold);
}
