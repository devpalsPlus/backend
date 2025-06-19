package hs.kr.backend.devpals.domain.project.repository;

import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepoisitory extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByProjectId(Long projectId);
    List<CommentEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<CommentEntity> findByUser(UserEntity user);
}
