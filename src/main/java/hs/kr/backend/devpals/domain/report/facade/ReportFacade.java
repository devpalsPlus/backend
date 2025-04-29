package hs.kr.backend.devpals.domain.report.facade;

import hs.kr.backend.devpals.domain.report.dto.ReportTagRequest;
import hs.kr.backend.devpals.domain.report.entity.ReportTagEntity;
import hs.kr.backend.devpals.domain.report.repository.ReportTagRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportFacade {

    private final ReportTagRepository reportTagRepository;
    private final Map<Long, ReportTagEntity> reportTagCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        refreshReportTags();
    }

    private void refreshReportTags() {
        List<ReportTagEntity> reportTags = reportTagRepository.findAll();
        reportTagCache.clear();
        reportTagCache.putAll(reportTags.stream().collect(Collectors.toMap(ReportTagEntity::getId, tag -> tag)));
    }

    public List<ReportTagEntity> getReportTag(){
        return List.copyOf(reportTagCache.values());
    }

    public ResponseEntity<ApiResponse<List<ReportTagEntity>>> getReportTags() {
        List<ReportTagEntity> reportTagEntities = List.copyOf(reportTagCache.values());
        ApiResponse<List<ReportTagEntity>> response = new ApiResponse<>(true, "신고사유(카테고리) 목록 가져오기 성공", reportTagEntities);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<String>> deleteReportTag(Long reportTagId) {
        ReportTagEntity reportTag = reportTagRepository.findById(reportTagId)
                .orElseThrow(() -> new CustomException(ErrorException.REPORT_TAG_NOT_FOUND));

        reportTagRepository.delete(reportTag);
        reportTagCache.remove(reportTagId);

        ApiResponse<String> response = new ApiResponse<>(true, "신고사유(카테고리) 삭제 성공", null);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<ReportTagEntity>> createReportTag(ReportTagRequest request) {
        ReportTagEntity reportTag = new ReportTagEntity(request.getName());
        ReportTagEntity saved = reportTagRepository.save(reportTag);
        refreshReportTags();
        return ResponseEntity.ok(new ApiResponse<>(true, "신고사유(카테고리) 등록 성공", saved));
    }

}
