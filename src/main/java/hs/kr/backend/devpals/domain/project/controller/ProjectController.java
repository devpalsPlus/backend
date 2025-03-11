package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.*;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectAllDto>>> getProjectAll(){
        return projectService.getProjectAll();
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<ProjectCountResponse>> getProjectCount(){
        return projectService.getProjectCount();
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectAllDto>> updateProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization")  String token,
            @RequestBody ProjectAllDto request) {
        return projectService.updateProject(projectId, token, request);
    }


    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProject(@RequestBody ProjectAllDto request) {
        return projectService.projectSignup(request);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectMainResponse>> getProjectList(
            @PathVariable Long projectId){
        return projectService.getProjectList(projectId);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ProjectMineResponse>>> getMyProjectList(@RequestHeader("Authorization")  String token){
        return projectService.getMyProject(token);
    }

}
