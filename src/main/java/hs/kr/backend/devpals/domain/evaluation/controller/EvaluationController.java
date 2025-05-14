package hs.kr.backend.devpals.domain.evaluation.controller;

import hs.kr.backend.devpals.domain.evaluation.dto.EvaluationRequest;
import hs.kr.backend.devpals.domain.evaluation.dto.EvaluationResponse;
import hs.kr.backend.devpals.domain.evaluation.service.EvaluationService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluation API", description = "프로젝트 참여자 평가 관련 API")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    @Operation(
            summary = "프로젝트 참여자 평가 제출",
            description = "로그인된 사용자가 특정 프로젝트에 대해 참여자들을 평가합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 제출 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public ResponseEntity<ApiResponse<String>> submitEvaluations(
            @RequestHeader("Authorization") String token,
            @RequestBody EvaluationRequest evaluationRequest) {
        return evaluationService.submitEvaluations(token, evaluationRequest);
    }

    @GetMapping("/{projectId}/members")
    @Operation(
            summary = "프로젝트 참여자 조회 (평가 여부 포함)",
            description = "해당 프로젝트에 참여한 유저 리스트를 조회하며, 각 유저가 평가되었는지 여부도 함께 제공합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "참여자 조회 성공")
            }
    )
    public ResponseEntity<ApiResponse<EvaluationResponse>> getProjectMembersWithEvaluationStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId) {
        return evaluationService.getProjectMembersWithEvaluationStatus(token, projectId);
    }
}
