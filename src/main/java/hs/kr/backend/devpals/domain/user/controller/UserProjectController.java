package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectAuthoredResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMyResponse;
import hs.kr.backend.devpals.domain.user.service.UserProjectService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Tag(name = "User Project API", description = "유저 프로젝트 관련 API")
public class UserProjectController {
    private final UserProjectService userProjectService;

    @GetMapping("/joinProject")
    @Operation(summary = "본인이 참여한 프로젝트 조회", description = "본인이 참여한 프로젝트의 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "본인 참여 프로젝트 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "본인 참여 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"참여한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<ProjectMyResponse>>> getMyParticipatedProjects(@RequestHeader("Authorization") String token)
    {
        return userProjectService.getMyParticipatedProjects(token);
    }

    @GetMapping("/applications")
    @Operation(summary = "본인이 지원한 프로젝트 조회", description = "본인이 지원한 프로젝트를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "본인 지원 프로젝트 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "본인 지원 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"지원한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<ProjectApplyResponse>>> getMyProjectList(@RequestHeader("Authorization") String token){
        return userProjectService.getMyProjectApply(token);
    }

    @GetMapping("/project")
    @Operation(summary = "기획자가 등록한 프로젝트 목록", description = "기획자(본인)가 등록한 프로젝트 목록을 등록순으로 보여줍니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로젝트 목록 가져오기 성공")
    public ResponseEntity<ApiResponse<List<ProjectAuthoredResponse>>> getMyProject(
            @RequestHeader("Authorization") String token) {
        return userProjectService.getMyProject(token);
    }

    @Operation(
            summary = "상대방이 만든 프로젝트 조회",
            description = "특정 사용자가 직접 생성한 프로젝트 목록을 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "상대방이 만든 프로젝트 조회 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없습니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 사용자를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    @GetMapping("/{id}/project")
    public ResponseEntity<ApiResponse<List<ProjectMyResponse>>> getCreatedProjects(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return userProjectService.getOnlyCreatedProjects(token, id);
    }

    @Operation(
            summary = "상대방이 참여한 프로젝트 조회",
            description = "특정 사용자가 참여한 프로젝트 목록을 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "상대방 참여 프로젝트 조회 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없습니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 사용자를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    @GetMapping("/{id}/participated")
    public ResponseEntity<ApiResponse<List<ProjectMyResponse>>> getParticipatedProjects(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return userProjectService.getOnlyParticipatedProjects(token, id);
    }
}
