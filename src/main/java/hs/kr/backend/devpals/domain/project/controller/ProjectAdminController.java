package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.FullApplicantInfoResponse;
import hs.kr.backend.devpals.domain.project.service.ProjectAdminService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/project")
@Tag(name = "Project Admin API", description = "관리자 전용 프로젝트 관련 API")
@RequiredArgsConstructor
public class ProjectAdminController {
    private final ProjectAdminService projectAdminService;

    @GetMapping("/{projectId}/full")
    @Operation(
            summary = "프로젝트 지원 정보 전체 조회 (관리자 전용)",
            description = "해당 프로젝트의 전체 지원자 목록, 특정 지원자의 상세 정보, 합불 결과를 한번에 조회합니다.",
            parameters = {
                    @Parameter(name = "projectId", description = "조회할 프로젝트 ID", example = "1"),
                    @Parameter(name = "applicantId", description = "상세 정보를 볼 지원자 ID", example = "5")
            }
    )
    public ResponseEntity<ApiResponse<FullApplicantInfoResponse>> getFullApplicants(
            @PathVariable Long projectId,
            @RequestParam Long applicantId,
            @RequestHeader("Authorization") String token) {
        return projectAdminService.getFullApplicantInfo(projectId, applicantId, token);
    }
}
