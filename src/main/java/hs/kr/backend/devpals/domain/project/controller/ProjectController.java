package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.service.ProjectService;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "Project API", description = "프로젝트 관련 API")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "프로젝트 조회", description = "프로젝트의 전체 리스트를 조회합니다")
    @ApiResponse(responseCode = "200", description = "프로젝트 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"프로젝트를 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectAllDto>>> getProjectAll(
            @RequestParam(required = false) List<Long> skillTag,
            @RequestParam(required = false) Long positionTag,
            @RequestParam(required = false) List<Long> methodType,
            @RequestParam(required = false) Boolean isBeginner,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return projectService.getProjectAll(skillTag, positionTag, methodType, isBeginner, keyword, page, size);
    }

    @GetMapping("/count")
    @Operation(summary = "프로젝트 개수 조회", description = "프로젝트의 개수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 개수 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로젝트 개수 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"프로젝트 개수를 조회할 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<ProjectCountResponse>> getProjectCount() {
        return projectService.getProjectCount();
    }

    @GetMapping("/my")
    @Operation(summary = "기획자가 등록한 프로젝트 목록", description = "기획자(본인)가 등록한 프로젝트 목록을 등록순으로 보여줍니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 목록 가져오기 성공")
    public ResponseEntity<ApiCustomResponse<List<ProjectAuthoredResponse>>> getMyProject(
            @RequestHeader("Authorization") String token) {
        return projectService.getMyProject(token);
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 업데이트", description = "프로젝트를 업데이트 합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 업데이트 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로젝트 업데이트 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"작성자만 수정 가능합니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> updateProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token,
            @RequestBody ProjectAllDto request) {
        return projectService.updateProject(projectId, token, request);
    }

    @PutMapping("/{projectId}/close")
    @Operation(summary = "공고 모집 종료", description = "기획자(본인)가 공고 모집을 종료합니다.")
    @ApiResponse(responseCode = "200", description = "공고 모집 종료 성공")
    @ApiResponse(
            responseCode = "400",
            description = "해당 공고 작성자(기획자)가 아닌 경우",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"기획자만 모집을 종료할 수 있습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<ProjectCloseResponse>> closeProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token) {
        return projectService.closeProject(projectId, token);
    }

    @PostMapping
    @Operation(summary = "프로젝트 작성", description = "프로젝트를 작성합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 작성 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로젝트 작성 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"프로젝트 등록에 실패했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> createProject(
            @RequestBody ProjectAllDto request,
            @RequestHeader("Authorization") String token) {
        return projectService.projectSignup(request, token);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 상세 내용", description = "해당 프로젝트의 상세 내용을 보여줍니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 상세 내용 데이터 제공 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로젝트 데이터를 불러올 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> getProjectList(
            @PathVariable Long projectId) {
        return projectService.getProjectList(projectId);
    }
}
