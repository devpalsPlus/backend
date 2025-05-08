package hs.kr.backend.devpals.domain.notice.repository;

import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
    List<NoticeEntity> findAllByOrderByCreatedAtDesc();
    Optional<NoticeEntity> findFirstByIdLessThanOrderByIdDesc(Long id);
    Optional<NoticeEntity> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}
