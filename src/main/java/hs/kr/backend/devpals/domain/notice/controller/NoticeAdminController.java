package hs.kr.backend.devpals.domain.notice.controller;

import hs.kr.backend.devpals.domain.notice.dto.NoticeDTO;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDetailResponse;
import hs.kr.backend.devpals.domain.notice.dto.NoticeListResponse;
import hs.kr.backend.devpals.domain.notice.service.NoticeAdminService;
import hs.kr.backend.devpals.domain.notice.service.NoticeService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
@Tag(name = "Notice Admin API", description = "공지사항 관리자 관련 API")
public class NoticeAdminController {
    private final NoticeAdminService noticeAdminService;

    @Operation(
            summary = "공지사항 등록",
            description = "관리자가 새로운 공지사항을 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 등록 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한이 없는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"관리자 권한이 필요합니다.\", \"data\": null}")
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createNotice(
            @RequestHeader("Authorization") String token,
            @RequestBody NoticeDTO noticeDTO) {
        return noticeAdminService.createNotice(token, noticeDTO);
    }

    @Operation(
            summary = "공지사항 수정",
            description = "기존 공지사항을 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한이 없는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"관리자 권한이 필요합니다.\", \"data\": null}")
                            )
                    )
            }
    )
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<String>> updateNotice(
            @RequestHeader("Authorization") String token,
            @PathVariable Long noticeId,
            @RequestBody NoticeDTO noticeDTO) {
        return noticeAdminService.updateNotice(token, noticeId, noticeDTO);
    }

    @Operation(
            summary = "공지사항 삭제",
            description = "특정 공지사항을 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 삭제 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한이 없는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"관리자 권한이 필요합니다.\", \"data\": null}")
                            )
                    )
            }
    )
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<String>> deleteNotice(
            @RequestHeader("Authorization") String token,
            @PathVariable Long noticeId) {
        return noticeAdminService.deleteNotice(token, noticeId);
    }
}
