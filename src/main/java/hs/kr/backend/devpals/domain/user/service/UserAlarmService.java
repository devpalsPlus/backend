package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.user.dto.AlarmDto;
import hs.kr.backend.devpals.domain.user.entity.AlramEntity;
import hs.kr.backend.devpals.domain.user.repository.AlarmRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAlarmService {

    private final JwtTokenValidator jwtTokenValidator;
    private final AlarmRepository alarmRepository;
    private final Map<Long, List<AlarmDto>> alarmMyCache = new HashMap<>();

    public ResponseEntity<ApiResponse<List<AlarmDto>>> getUserAlarm(String token, Integer filterVal) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<AlarmDto> cachedAlrams = alarmMyCache.get(userId);
        if (cachedAlrams == null) {
            cachedAlrams = alarmRepository.findByUserId(userId).stream()
                    .map(AlarmDto::fromEntity)
                    .collect(Collectors.toList());
            alarmMyCache.put(userId, cachedAlrams);
        }


        if(!AlramFilter.isValid(filterVal))
            throw new CustomException(ErrorException.ALARM_FILTER_NOT_FOUND);

        List<AlarmDto> filtered = (Objects.equals(filterVal, AlramFilter.ALL.getValue()))
                ? cachedAlrams
                : cachedAlrams.stream()
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
        AlramEntity alramEntity
                = alarmRepository.findByUserIdAndAlarmId(userId, alarmId).orElseThrow(() -> new CustomException(ErrorException.ALARM_NOT_FOUND));
        if(alramEntity.getAlramFilter().equals(AlramFilter.APPLIED_PROJECTS))
            throw new CustomException(ErrorException.CAN_NOT_DELETE_ALARM);
        alarmRepository.delete(alramEntity);
        ApiResponse<String> response = new ApiResponse<>(true, "알람 삭제 성공", null);
        return ResponseEntity.ok(response);
    }
}
