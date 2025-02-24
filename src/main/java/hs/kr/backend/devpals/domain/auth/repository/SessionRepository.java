package hs.kr.backend.devpals.domain.auth.repository;

import hs.kr.backend.devpals.domain.auth.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Integer> {
    Optional<SessionEntity> findByRefreshToken(String refreshToken);

    void deleteByUserId(Integer userId);
}
