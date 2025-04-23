package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.CommentDTO;
import hs.kr.backend.devpals.domain.project.dto.RecommentDTO;
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

    @PatchMapping("/{projectId}/comment/{commentId}")
    @Operation(summary = "댓글 업데이트", description = "해당 프로젝트의 댓글을 업데이트합니다. 댓글 작성자 또는 프로젝트 작성자만 업데이트 가능합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 업데이트 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "댓글 삭제 실패 - 권한 없음 또는 댓글 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"업데이트 권한이 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> updateComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId,
            @PathVariable Long commentId,
            @RequestParam String content) {
        return projectCommentService.updateComment(token, projectId, commentId, content);
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

    @PostMapping("/{projectId}/comment/{commentId}/recomment")
    @Operation(summary = "대댓글 작성", description = "특정 댓글에 대댓글을 작성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "대댓글 작성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "대댓글 작성 실패 - 잘못된 요청 또는 인증 오류",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"대댓글 내용이 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> writeRecomment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId,
            @PathVariable Long commentId,
            @RequestBody RecommentDTO dto) {
        return projectCommentService.writeRecomment(token, projectId, commentId, dto);
    }

    @GetMapping("/{projectId}/comment/{commentId}/recomment")
    @Operation(summary = "대댓글 목록 조회", description = "특정 댓글에 달린 대댓글들을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "대댓글 목록 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "대댓글 목록 조회 실패 - 존재하지 않는 댓글",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"존재하지 않는 댓글입니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<RecommentDTO>>> getRecomments(@PathVariable Long commentId) {
        return projectCommentService.getRecomments(commentId);
    }

    @PatchMapping("/{projectId}/recomment/{recommentId}")
    @Operation(summary = "대댓글 수정", description = "작성한 대댓글을 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "대댓글 수정 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "대댓글 수정 실패 - 권한 없음 또는 대댓글 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"수정 권한이 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> updateRecomment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long recommentId,
            @RequestBody RecommentDTO dto) {
        return projectCommentService.updateRecomment(token, recommentId, dto);
    }

    @DeleteMapping("/{projectId}/recomment/{recommentId}")
    @Operation(summary = "대댓글 삭제", description = "작성한 대댓글을 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "대댓글 삭제 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "대댓글 삭제 실패 - 권한 없음 또는 대댓글 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"삭제 권한이 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> deleteRecomment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long recommentId) {
        return projectCommentService.deleteRecomment(token, recommentId);
    }
}