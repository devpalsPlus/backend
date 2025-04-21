package hs.kr.backend.devpals.domain.user.repository;

import hs.kr.backend.devpals.domain.user.entity.AlramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<AlramEntity, Long> {
    List<AlramEntity> findByUserId(Long userId);
}
