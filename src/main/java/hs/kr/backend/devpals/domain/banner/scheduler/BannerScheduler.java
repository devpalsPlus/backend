package hs.kr.backend.devpals.domain.banner.scheduler;

import hs.kr.backend.devpals.domain.banner.entity.BannerEntity;
import hs.kr.backend.devpals.domain.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BannerScheduler {
    private final BannerRepository bannerRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateExpiredBanners() {
        LocalDateTime now = LocalDateTime.now();
        List<BannerEntity> expiredBanners = bannerRepository.findByEndDateBeforeAndVisibleTrue(now);

        for (BannerEntity banner : expiredBanners) {
            banner.setInvisible();
        }
    }
}
