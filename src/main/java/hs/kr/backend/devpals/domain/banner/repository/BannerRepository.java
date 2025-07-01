package hs.kr.backend.devpals.domain.banner.repository;

import hs.kr.backend.devpals.domain.banner.entity.BannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {
    List<BannerEntity> findAllByIsVisibleTrue();

    List<BannerEntity> findByEndDateBeforeAndIsVisibleTrue(LocalDateTime now);
}
