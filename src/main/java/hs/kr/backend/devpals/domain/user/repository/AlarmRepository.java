package hs.kr.backend.devpals.domain.user.repository;

import hs.kr.backend.devpals.domain.user.entity.AlramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<AlramEntity, Long> {
    List<AlramEntity> findByUserId(Long userId);

    @Query("SELECT a FROM AlramEntity a WHERE a.user.id = :userId AND a.id = :alarmId")
    Optional<AlramEntity> findByUserIdAndAlarmId(@Param("userId") Long userId, @Param("alarmId") Long alarmId);

    @Modifying
    @Query("DELETE FROM AlramEntity a WHERE a.createdAt < :threshold")
    void deleteAllOlderThan(@Param("threshold") LocalDateTime threshold);
}
