package hs.kr.backend.devpals.domain.banner.repository;

import hs.kr.backend.devpals.domain.banner.entity.BannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {
    List<BannerEntity> findAllByIsVisibleTrue();
}
