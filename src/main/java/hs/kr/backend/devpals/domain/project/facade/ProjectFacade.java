package hs.kr.backend.devpals.domain.project.facade;

import hs.kr.backend.devpals.domain.project.dto.MethodTypeResponse;
import hs.kr.backend.devpals.domain.project.entity.MethodTypeEntity;
import hs.kr.backend.devpals.domain.project.repository.MethodTypeRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectFacade {

    private final MethodTypeRepository methodTypeRepository;

    private final Map<Long, MethodTypeEntity> methodTypeCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        refreshMethodTypes();
    }

    private void refreshMethodTypes() {
        List<MethodTypeEntity> methodTypes = methodTypeRepository.findAll();

        methodTypeCache.clear();
        methodTypeCache.putAll(methodTypes.stream().collect(Collectors.toMap(MethodTypeEntity::getId, methodType -> methodType)));
    }

    public ResponseEntity<ApiResponse<List<MethodTypeEntity>>> getMethodType() {
        List<MethodTypeEntity> methodTypes = List.copyOf(methodTypeCache.values());
        ApiResponse<List<MethodTypeEntity>> response = new ApiResponse<>(200, true, "방식 유형 목록 가져오기 성공", methodTypes);
        return ResponseEntity.ok(response);
    }

    public MethodTypeEntity getMethodTypeById(Long id) {
        return methodTypeCache.get(id);
    }

    // 메소드 태그 변환 (ID 리스트 -> DTO 리스트)
    public MethodTypeResponse getMethodTypeResponse(Long methodTypeId) {

        return MethodTypeResponse.fromEntity(getMethodTypeById(methodTypeId));
    }

}
