package hs.kr.backend.devpals.domain.user.schedule;

import hs.kr.backend.devpals.domain.user.service.UserAlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserScheduleService {

    private final UserAlarmService userAlarmService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void deleteAlarmBeforeOneWeek(){
        userAlarmService.deleteAlarmOneWeekBefore();
    }

}
