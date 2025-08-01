package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectAllDto;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.AlarmDto;
import hs.kr.backend.devpals.domain.user.dto.AlarmRequest;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.entity.alarm.AlarmEntity;
import hs.kr.backend.devpals.domain.user.repository.AlarmRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final AlarmRepository alarmRepository;
    private final Map<Long, List<AlarmDto>> alarmMyCache = new HashMap<>();

    public ResponseEntity<ApiResponse<AlarmDto>> putAlarm(String token, AlarmRequest alarmRequest) {
        Long receiverId = jwtTokenValidator.getUserId(token);
        AlarmEntity alarm = alarmRepository.findByReceiverIdAndAlarmId(receiverId, alarmRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorException.ALARM_NOT_FOUND));
        alarm.updateEnabled(alarmRequest.getEnabled());

        refreshCacheUserAlarm(receiverId);

        alarmRepository.save(alarm);

        ApiResponse<AlarmDto> response = new ApiResponse<>(200, true, "알람 수정 성공", AlarmDto.fromEntity(alarm));
        return ResponseEntity.ok(response);
    }

    public void refreshCacheUserAlarm(Long receiverId) {
        List<AlarmEntity> byReceiverId = alarmRepository.findByReceiverId(receiverId);
        List<AlarmDto> cachedAlarm = byReceiverId.stream()
                .map(AlarmDto::fromEntity)
                .collect(Collectors.toList());
        alarmMyCache.put(receiverId, cachedAlarm);
    }

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

        String answer = reverseSorted.isEmpty() ? "알림이 존재하지 않습니다." : "알림 조회 성공";
        reverseSorted = reverseSorted.isEmpty() ? null : reverseSorted;
        return ResponseEntity.ok(new ApiResponse<>(200, true, answer, reverseSorted));
    }

    @Transactional
    public void deleteAlarmOneWeekBefore(){
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(30);
        // 일주일 이전 알람이 있는 사용자 ID만 가져오기
        List<Long> affectedUserIds = alarmRepository.findUserIdsWithAlarmsOlderThan(sevenDaysAgo);
        affectedUserIds.forEach(this::refreshCacheUserAlarm); // 성능 고민필요
        alarmRepository.deleteAllOlderThanExceptApplied(sevenDaysAgo);
    }

    public ResponseEntity<ApiResponse<String>> deleteAlarm(String token, Long alarmId) {
        Long userId = jwtTokenValidator.getUserId(token);
        AlarmEntity alarmEntity
                = alarmRepository.findByReceiverIdAndAlarmId(userId, alarmId).orElseThrow(() -> new CustomException(ErrorException.ALARM_NOT_FOUND));

        //신고알람은 삭제 안되게 설정
        if(alarmEntity.getAlarmFilterIntValue().equals(AlarmFilter.REPORT.getValue())) {
            throw new CustomException(ErrorException.CAN_NOT_DELETE_ALARM);
        }
        alarmRepository.delete(alarmEntity);

        refreshCacheUserAlarm(userId);
        ApiResponse<String> response = new ApiResponse<>(200, true, "알람 삭제 성공", null);
        return ResponseEntity.ok(response);
    }

}
