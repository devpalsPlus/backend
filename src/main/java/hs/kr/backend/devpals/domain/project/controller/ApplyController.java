package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyRequest;
import hs.kr.backend.devpals.domain.project.service.ApplyService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applicantService;
    @PostMapping("/{projectId}/apply")
    public ResponseEntity<ApiResponse<String>> projectApply(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token,
            @RequestBody ProjectApplyRequest request){
        return applicantService.projectApply(projectId, request, token);
    }
}
