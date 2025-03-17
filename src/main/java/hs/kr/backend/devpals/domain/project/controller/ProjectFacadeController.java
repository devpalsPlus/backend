package hs.kr.backend.devpals.domain.project.controller;

import hs.kr.backend.devpals.domain.project.entity.MethodTypeEntity;
import hs.kr.backend.devpals.domain.project.facade.ProjectFacade;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping
@Tag(name = "MethodType API", description = "방식 종류를 가져오는 API")
public class ProjectFacadeController {

    private final ProjectFacade projectFacade;

    @GetMapping("/method-type")
    @Operation(summary = "모든 방식 종류 조회", description = "저장된 방식 종류 데이터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "방식 종류 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "방식 종류 목록 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"방식 종류 목록을 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<MethodTypeEntity>>> getMethodType() {
        return projectFacade.getMethodType();
    }
}
