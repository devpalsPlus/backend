package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.user.dto.AlarmDto;
import hs.kr.backend.devpals.domain.user.repository.AlarmRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAlarmService {

    private final JwtTokenValidator jwtTokenValidator;
    private final AlarmRepository alarmRepository;
    private final Map<Long, List<AlarmDto>> alarmMyCache = new HashMap<>();

    public ResponseEntity<ApiResponse<List<AlarmDto>>> getUserAlarm(String token, String filterStr) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<AlarmDto> cachedAlrams = alarmMyCache.get(userId);
        if (cachedAlrams == null) {
            cachedAlrams = alarmRepository.findByUserId(userId).stream()
                    .map(AlarmDto::fromEntity)
                    .collect(Collectors.toList());
            alarmMyCache.put(userId, cachedAlrams);
        }

        AlramFilter tempFilter = null;
        if (filterStr != null && !filterStr.isBlank()) {
            try {
                tempFilter = AlramFilter.fromDisplayName(filterStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "유효하지 않은 필터 값입니다.", null));
            }
        }

        final AlramFilter filter = tempFilter;

        List<AlarmDto> filtered = (filter == null || filter == AlramFilter.ALL)
                ? cachedAlrams
                : cachedAlrams.stream()
                .filter(a -> a.getAlramFilter() == filter)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "알림 조회 성공", filtered));
    }
}
