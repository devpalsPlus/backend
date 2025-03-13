package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplicantResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplyRequest;
import hs.kr.backend.devpals.domain.project.service.ApplyService;
import hs.kr.backend.devpals.global.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<String>> projectApply(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token,
            @RequestBody ProjectApplyRequest request){
        return applicantService.projectApply(projectId, request, token);
    }

    @GetMapping("/{projectId}/applicants")
    public ResponseEntity<ApiResponse<List<ProjectApplicantResponse>>> getProjectApplicantList(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token){
        return applicantService.getProjectApplicantList(projectId, token);
    }
}
