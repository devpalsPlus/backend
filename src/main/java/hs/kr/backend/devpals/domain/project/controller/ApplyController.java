package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplicantResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplicantResultResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplyRequest;
import hs.kr.backend.devpals.domain.project.service.ApplyService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@Tag(name = "Project Apply API", description = "프로젝트 지원 관련 API")
public class ApplyController {

    private final ApplyService applicantService;

    @PostMapping("/{projectId}/apply")
    @Operation(summary = "프로젝트 지원하기",description = "지원자가 프로젝트를 지원합니다.")
    public ResponseEntity<ApiResponse<String>> projectApply(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token,
            @RequestBody ProjectApplyRequest request){
        return applicantService.projectApply(projectId, request, token);
    }

    @GetMapping("/{projectId}/applicants")
    @Operation(summary = "프로젝트 지원자 목록", description = "관리자(본인)가 지원자 목록을 확인합니다.")
    public ResponseEntity<ApiResponse<List<ProjectApplicantResponse>>> getProjectApplicants(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token){
        return applicantService.getProjectApplicants(projectId, token);
    }

    @GetMapping("/{projectId}/applicants/results")
    @Operation(summary = "프로젝트 지원 결과 목록", description = "관리자(본인)가 (지원결과) 목록을 확인합니다.")
    public ResponseEntity<ApiResponse<ProjectApplicantResultResponse>> getProjectApplicantsResults(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token){
        return applicantService.getProjectApplicantResults(projectId, token);
    }
}
