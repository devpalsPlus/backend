package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.service.ProjectService;
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
@Tag(name = "Project API", description = "프로젝트 관련 API")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "프로젝트 조회", description = "프로젝트의 전체 리스트를 조회합니다")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로젝트 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"프로젝트를 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ProjectListResponse>> getProjectAll(
            @RequestParam(required = false, defaultValue = "") List<Long> skillTag,
            @RequestParam(defaultValue = "0") Long positionTag,
            @RequestParam(defaultValue = "0") Long methodType,
            @RequestParam(defaultValue = "false") Boolean isBeginner,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page) {
        return projectService.getProjectAll(skillTag, positionTag, methodType, isBeginner, keyword, page);
    }

    @GetMapping("/count")
    @Operation(summary = "프로젝트 개수 조회", description = "프로젝트의 개수를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로젝트 개수 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "프로젝트 개수 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"프로젝트 개수를 조회할 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ProjectCountResponse>> getProjectCount() {
        return projectService.getProjectCount();
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 업데이트", description = "프로젝트를 업데이트 합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로젝트 업데이트 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "프로젝트 업데이트 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"작성자만 수정 가능합니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ProjectAllDto>> updateProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token,
            @RequestBody ProjectAllDto request) {
        return projectService.updateProject(projectId, token, request);
    }

    @PutMapping("/{projectId}/close")
    @Operation(summary = "공고 모집 종료", description = "기획자(본인)가 공고 모집을 종료합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 모집 종료 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "해당 공고 작성자(기획자)가 아닌 경우",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"기획자만 모집을 종료할 수 있습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ProjectCloseResponse>> closeProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token) {
        return projectService.closeProject(projectId, token);
    }

    @PostMapping
    @Operation(summary = "프로젝트 작성", description = "프로젝트를 작성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로젝트 작성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "프로젝트 작성 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"프로젝트 등록에 실패했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ProjectAllDto>> createProject(
            @RequestBody ProjectAllDto request,
            @RequestHeader("Authorization") String token) {
        return projectService.projectSignup(request, token);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 상세 내용", description = "해당 프로젝트의 상세 내용을 보여줍니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로젝트 상세 내용 데이터 제공 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "프로젝트 데이터를 불러올 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<ProjectAllDto>> getProjectList(
            @PathVariable Long projectId) {
        return projectService.getProjectDetail(projectId);
    }
}
