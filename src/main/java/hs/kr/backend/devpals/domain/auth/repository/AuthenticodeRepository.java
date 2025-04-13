package hs.kr.backend.devpals.domain.auth.repository;

import hs.kr.backend.devpals.domain.auth.entity.EmailVertificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticodeRepository extends JpaRepository<EmailVertificationEntity, Long> {

    Optional<EmailVertificationEntity> findTopByUserEmailOrderByExpiresAtDesc(String email);

    void deleteByUserEmail(String email);
}