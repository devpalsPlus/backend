package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.CommentDTO;
import hs.kr.backend.devpals.domain.project.service.ProjectCommentService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "Project Comment API", description = "프로젝트 댓글 관련 API")
public class ProjectCommentController {

    private final ProjectCommentService projectCommentService;

    @PostMapping("/{projectId}/comment")
    @Operation(summary = "댓글 작성", description = "특정 프로젝트에 댓글을 작성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 작성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "댓글 작성 실패 - 잘못된 요청 또는 인증 오류",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"댓글 내용이 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> writeComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId,
            @RequestBody CommentDTO commentDTO) {
        return projectCommentService.writeComment(token, projectId, commentDTO);
    }

    @GetMapping("/{projectId}/comment")
    @Operation(summary = "댓글 목록 조회", description = "특정 프로젝트에 작성된 댓글들을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "댓글 목록 조회 실패 - 존재하지 않는 프로젝트",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"존재하지 않는 프로젝트입니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getComments(
            @PathVariable Long projectId) {
        return projectCommentService.getComment(projectId);
    }

    @DeleteMapping("/{projectId}/comment/{commentId}")
    @Operation(summary = "댓글 삭제", description = "해당 프로젝트의 댓글을 삭제합니다. 댓글 작성자 또는 프로젝트 작성자만 삭제 가능합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "댓글 삭제 실패 - 권한 없음 또는 댓글 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"삭제 권한이 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId,
            @PathVariable Long commentId) {
        return projectCommentService.deleteComment(token, projectId, commentId);
    }
}