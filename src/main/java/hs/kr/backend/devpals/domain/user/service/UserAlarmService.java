package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.user.dto.AlarmDto;
import hs.kr.backend.devpals.domain.user.entity.alarm.AlarmEntity;
import hs.kr.backend.devpals.domain.user.repository.AlarmRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.AlarmFilter;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAlarmService {

    private final JwtTokenValidator jwtTokenValidator;
    private final AlarmRepository alarmRepository;
    private final Map<Long, List<AlarmDto>> alarmMyCache = new HashMap<>();

    public ResponseEntity<ApiResponse<List<AlarmDto>>> getUserAlarm(String token, Integer filterVal) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<AlarmDto> cachedAlarms = alarmMyCache.get(userId);
        if (cachedAlarms == null) {
            cachedAlarms = alarmRepository.findByReceiverId(userId).stream()
                    .map(AlarmDto::fromEntity)
                    .collect(Collectors.toList());
            alarmMyCache.put(userId, cachedAlarms);
        }


        if(!AlarmFilter.isValid(filterVal))
            throw new CustomException(ErrorException.ALARM_FILTER_NOT_FOUND);

        List<AlarmDto> filtered = (Objects.equals(filterVal, AlarmFilter.ALL.getValue()))
                ? cachedAlarms
                : cachedAlarms.stream()
                .filter(a -> Objects.equals(a.getAlarmFilterId(), filterVal))
                .toList();

        List<AlarmDto> reverseSorted
                = filtered.stream().sorted(Comparator.comparing(AlarmDto::getCreatedAt).reversed()).toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "알림 조회 성공", reverseSorted));
    }

    @Transactional
    public void deleteAlarmOneWeekBefore(){
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        alarmRepository.deleteAllOlderThanExceptApplied(sevenDaysAgo);
    }

    public ResponseEntity<ApiResponse<String>> deleteAlarm(String token, Long alarmId) {
        Long userId = jwtTokenValidator.getUserId(token);
        AlarmEntity alarmEntity
                = alarmRepository.findByUserIdAndAlarmId(userId, alarmId).orElseThrow(() -> new CustomException(ErrorException.ALARM_NOT_FOUND));
        if(alarmEntity.getAlarmFilterIntValue().equals(AlarmFilter.APPLIED_PROJECTS.getValue()))
            throw new CustomException(ErrorException.CAN_NOT_DELETE_ALARM);
        alarmRepository.delete(alarmEntity);
        ApiResponse<String> response = new ApiResponse<>(true, "알람 삭제 성공", null);
        return ResponseEntity.ok(response);
    }
}
