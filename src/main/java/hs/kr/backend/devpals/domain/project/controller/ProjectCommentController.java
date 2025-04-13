package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.CommentDTO;
import hs.kr.backend.devpals.domain.project.service.ProjectCommentService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectCommentController {

    private final ProjectCommentService projectCommentService;

    @PostMapping("/{projectId}/comment")
    public ResponseEntity<ApiResponse<String>> writeComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId,
            @RequestBody CommentDTO commentDTO) {
        return projectCommentService.writeComment(token, projectId, commentDTO);
    }
}
