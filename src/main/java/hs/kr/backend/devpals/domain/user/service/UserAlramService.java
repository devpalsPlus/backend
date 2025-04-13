package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectMineResponse;
import hs.kr.backend.devpals.domain.user.dto.AlramDTO;
import hs.kr.backend.devpals.domain.user.entity.AlramEntity;
import hs.kr.backend.devpals.domain.user.repository.AlramRepository;
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
public class UserAlramService {

    private final JwtTokenValidator jwtTokenValidator;
    private final AlramRepository alramRepository;
    private final Map<Long, List<AlramDTO>> alramMyCache = new HashMap<>();

    public ResponseEntity<ApiResponse<List<AlramDTO>>> getUserAlram(String token, String filterStr) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<AlramDTO> cachedAlrams = alramMyCache.get(userId);
        if (cachedAlrams == null) {
            cachedAlrams = alramRepository.findByUser_Id(userId).stream()
                    .map(AlramDTO::fromEntity)
                    .collect(Collectors.toList());
            alramMyCache.put(userId, cachedAlrams);
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

        List<AlramDTO> filtered = (filter == null || filter == AlramFilter.ALL)
                ? cachedAlrams
                : cachedAlrams.stream()
                .filter(a -> a.getAlramFilter() == filter)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "알림 조회 성공", filtered));
    }
}
