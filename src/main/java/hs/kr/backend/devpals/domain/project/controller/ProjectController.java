package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.service.ProjectService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProject(@RequestBody ProjectAllRequest request) {
        return projectService.projectSignup(request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectMainResponse>>> getProjectList(){
        return projectService.getProjectList();
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> getProjectDetail(
            @PathVariable Long projectId,
            @RequestBody ProjectMainRequest projectMain){
        return projectService.getProjectDetail(projectId, projectMain);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<String>> updateProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization")  String token,
            @RequestBody ProjectAllRequest request) {
        return projectService.updateProject(projectId, token, request);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<ProjectCountResponse>> getProjectCount(){
        return projectService.getProjectCount();
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ProjectMainResponse>>> getMyProjectList(){}

}
