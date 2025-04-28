package hs.kr.backend.devpals.domain.report.facade;

import hs.kr.backend.devpals.domain.report.entity.ReportTagEntity;
import hs.kr.backend.devpals.domain.report.repository.ReportTagRepository;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReportFacade {

    private final ReportTagRepository reportTagRepository;
    private final Map<Long, ReportTagEntity> reportTagCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        refreshReportTagCache();
    }

    private void refreshReportTagCache() {
        List<ReportTagEntity> reportTags = reportTagRepository.findAll();
        reportTagCache.clear();
        reportTagCache.putAll(reportTags.stream().collect(Collectors.toMap(ReportTagEntity::getId, tag -> tag)));
    }

    public ResponseEntity<ApiResponse<List<ReportTagEntity>>> getReportTag() {
        List<ReportTagEntity> reportTagEntities = List.copyOf(reportTagCache.values());
        ApiResponse<List<ReportTagEntity>> response = new ApiResponse<>(true, "신고사유(카테고리) 목록 가져오기 성공", reportTagEntities);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<String>> deleteReportTag(Long positionTagId) {
        PositionTagEntity positionTag = reportTagRepository.findById(positionTagId)
                .orElseThrow(() -> new CustomException(ErrorException.POSITION_NOT_FOUND));

        positionTagRepository.delete(positionTag);
        positionTagCache.remove(positionTagId);

        ApiResponse<String> response = new ApiResponse<>(true, "포지션 태그 삭제 성공", null);
        return ResponseEntity.ok(response);
    }


}
