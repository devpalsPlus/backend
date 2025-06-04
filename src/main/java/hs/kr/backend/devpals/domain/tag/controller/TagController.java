package hs.kr.backend.devpals.domain.tag.controller;

import hs.kr.backend.devpals.domain.tag.dto.PositionTagRequest;
import hs.kr.backend.devpals.domain.tag.dto.SkillTagRequest;
import hs.kr.backend.devpals.domain.tag.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.tag.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.tag.service.TagService;
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

@AllArgsConstructor
@RestController
@RequestMapping
@Tag(name = "Skill,Position API", description = "스킬, 포지션을 가져오는 API")
public class TagController {

    private final TagService tagService;

    @GetMapping("/position-tag")
    @Operation(summary = "모든 포지션 조회", description = "저장된 모든 포지션 데이터를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포지션 목록 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "포지션 목록 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"포지션 목록을 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<PositionTagEntity>>> getPositionTag() {
        return tagService.getPositionTag();
    }

    @GetMapping("/skill-tag")
    @Operation(summary = "모든 스킬 조회", description = "저장된 모든 스킬 데이터를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스킬 목록 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "스킬 목록 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"스킬 목록을 불러오는 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<SkillTagEntity>>> getSkillTag() {
        return tagService.getSkillTags();
    }

    @PostMapping("/position-tag")
    @Operation(summary = "포지션 태그 등록", description = "포지션 태그를 저장합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포지션 태그 저장 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "포지션 태그 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"포지션 태그를 저장하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<PositionTagEntity>> createPositionTag(@RequestBody PositionTagRequest request) {
        return tagService.createPositionTag(request);
    }

    @PostMapping("/skill-tag")
    @Operation(summary = "스킬 태그 등록", description = "스킬 태그를 저장합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스킬 태그 저장 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "스킬 태그 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"스킬 태그를 저장하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<SkillTagEntity>> createSkillTag(@ModelAttribute SkillTagRequest request) {
        return tagService.createSkillTag(request);
    }

    @DeleteMapping("/position-tag")
    @Operation(summary = "포지션 태그 삭제", description = "포지션 태그를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포지션 태그 삭제 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "포지션 태그 삭제 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"포지션 태그를 삭제하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> deletePositionTag(@RequestParam Long positionTagId) {
        return tagService.deletePositionTag(positionTagId);
    }

    @DeleteMapping("/skill-tag")
    @Operation(summary = "스킬 태그 삭제", description = "스킬 태그를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스킬 태그 삭제 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "스킬 태그 삭제 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"스킬 태그를 삭제하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> deleteSkillTag(@RequestParam Long skillTagId) {
        return tagService.deleteSkillTag(skillTagId);
    }
}
