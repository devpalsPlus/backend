package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectRequest;
import hs.kr.backend.devpals.domain.project.dto.ProjectMainResponse;
import hs.kr.backend.devpals.domain.project.service.ProjectService;
import hs.kr.backend.devpals.global.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<Long>> createProject(@RequestBody ProjectRequest request) {
        return projectService.projectSignup(request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectMainResponse>>> getProjectList(){
        return projectService.getProjectList();
    }


    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<String>> updateProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization")  String token,
            @RequestBody ProjectRequest request) {
        return projectService.updateProject(projectId, token, request);
    }
}
