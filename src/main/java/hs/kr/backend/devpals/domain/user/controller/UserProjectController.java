package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMineResponse;
import hs.kr.backend.devpals.domain.user.service.UserProjectService;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserProjectController {
    private final UserProjectService userProjectService;

    @GetMapping("/project")
    @Operation(summary = "본인이 참여한 프로젝트 조회", description = "본인이 참여한 프로젝트의 조회합니다.")
    @ApiResponse(responseCode = "200", description = "본인 참여 프로젝트 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "본인 참여 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"참여한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectMineResponse>>> getMyProjects(@RequestHeader("Authorization") String token) {
        return userProjectService.getMyProject(token);
    }

    @GetMapping("/applications")
    @Operation(summary = "본인이 지원한 프로젝트 조회", description = "본인이 지원한 프로젝트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "본인 지원 프로젝트 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "본인 지원 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"지원한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectApplyResponse>>> getMyProjectList(@RequestHeader("Authorization") String token){
        return userProjectService.getMyProjectApply(token);
    }

    @GetMapping("/{id}/project")
    @Operation(summary = "상대방이 참여한 프로젝트 조회", description = "상대방이 참여한 프로젝트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상대방 참여 프로젝트 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "상대방 참여 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 유저가 참여한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectMineResponse>>> getUserProjects(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return userProjectService.getUserProject(token, id);
    }
}
