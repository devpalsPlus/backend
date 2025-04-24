package hs.kr.backend.devpals.domain.project.repository;

import hs.kr.backend.devpals.domain.project.entity.RecommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommentRepository extends JpaRepository<RecommentEntity, Long> {
    List<RecommentEntity> findAllByCommentId(Long commentId);
    int countByCommentId(Long commentId);
}
