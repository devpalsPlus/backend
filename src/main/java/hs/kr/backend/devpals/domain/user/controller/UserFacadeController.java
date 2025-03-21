package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.PositionTagRequest;
import hs.kr.backend.devpals.domain.user.dto.SkillTagRequest;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping
@Tag(name = "Skill,Position API", description = "스킬, 포지션을 가져오는 API")
public class UserFacadeController {

    private final UserFacade userFacade;

    @GetMapping("/position-tag")
    @Operation(summary = "모든 포지션 조회", description = "저장된 모든 포지션 데이터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "포지션 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "포지션 목록 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"포지션 목록을 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<PositionTagEntity>>> getPositionTag() {
        return userFacade.getPositionTag();
    }

    @GetMapping("/skill-tag")
    @Operation(summary = "모든 스킬 조회", description = "저장된 모든 스킬 데이터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "스킬 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "스킬 목록 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"스킬 목록을 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<SkillTagEntity>>> getSkillTag() {
        return userFacade.getSkillTags();
    }

    @PostMapping("/position-tag")
    @Operation(summary = "포지션 태그 등록", description = "포지션 태그를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "포지션 태그 저장 성공")
    @ApiResponse(
            responseCode = "400",
            description = "포지션 태그 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"포지션 태그를 저장하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<PositionTagEntity>> createPositionTag(@RequestBody PositionTagRequest request) {
        return userFacade.createPositionTag(request);
    }

    @PostMapping("/skill-tag")
    @Operation(summary = "스킬 태그 등록", description = "스킬 태그를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "스킬 태그 저장 성공")
    @ApiResponse(
            responseCode = "400",
            description = "스킬 태그 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"스킬 태그를 저장하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<SkillTagEntity>> createSkillTag(@RequestBody SkillTagRequest request) {
        return userFacade.createSkillTag(request);
    }
}
