package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.service.ApplyService;
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
@RequestMapping("/project")
@RequiredArgsConstructor
@Tag(name = "Project Apply API", description = "프로젝트 지원 관련 API")
public class ApplyController {

    private final ApplyService applicantService;

    @PostMapping("/{projectId}/apply")
    @Operation(summary = "프로젝트 지원", description = "프로젝트를 지원합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 지원 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로젝트 지원 실패 - 이미 지원했거나 존재하지 않는 프로젝트",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 프로젝트에 이미 지원하셨습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<String>> projectApply(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token,
            @RequestBody ProjectApplyRequest request) {
        return applicantService.projectApply(projectId, request, token);
    }

    @GetMapping("/{projectId}/applicants")
    @Operation(summary = "프로젝트 공고 지원자 조회", description = "프로젝트에 지원한 지원자들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "지원자 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "지원자 목록 조회 실패 - 해당 프로젝트가 존재하지 않거나 권한 없음",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 공고의 기획자만 조회 가능합니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectApplicantResponse>>> getProjectApplicantList(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token) {
        return applicantService.getProjectApplicantList(projectId, token);
    }

    @PutMapping("/{projectId}/applicant")
    @Operation(summary = "지원자 상태 변경", description = "관리자(본인)가 지원자의 상태를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "지원자의 상태 변경 성공")
    @ApiResponse(
            responseCode = "400",
            description = "해당 공고 작성자(기획자)가 아닌 경우",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"기획자만 수정 가능합니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<ApplicantStatusUpdateResponse>> modifyApplicantStatus(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token,
            @RequestBody ApplicantStatusUpdateRequest applicantStatusUpdateRequest){
        return applicantService.modifyApplicantStatus(projectId, token,applicantStatusUpdateRequest);
    }

    @GetMapping("/{projectId}/applicants/results")
    @Operation(summary = "프로젝트 지원 결과 목록", description = "관리자(본인)가 (지원결과) 목록을 확인합니다.")
    @ApiResponse(responseCode = "200", description = "프로젝트 지원 결과 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로젝트 지원 결과 목록 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"프로젝트 지원 결과를 조회할 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<ProjectApplicantResultResponse>> getProjectApplicantsResults(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String token){
        return applicantService.getProjectApplicantResults(projectId, token);
    }

}
