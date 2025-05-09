package hs.kr.backend.devpals.domain.notice.controller;

import hs.kr.backend.devpals.domain.notice.dto.NoticeDTO;
import hs.kr.backend.devpals.domain.notice.dto.NoticeDetailResponse;
import hs.kr.backend.devpals.domain.notice.dto.NoticeListResponse;
import hs.kr.backend.devpals.domain.notice.service.NoticeService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
@Tag(name = "Notice API", description = "공지사항 관련 API")
public class NoticeController {
    private final NoticeService noticeService;

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
        return noticeService.createNotice(token, noticeDTO);
    }

    @Operation(
            summary = "공지사항 전체 조회",
            description = "등록된 모든 공지사항을 최신순으로 페이징 처리하여 조회합니다.\n\n" +
                    "- `page`는 0부터 시작합니다.\n" +
                    "- `size`는 한 페이지당 보여줄 공지 개수입니다. (기본값 10)\n" +
                    "- `keyword`로 제목 검색이 가능합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 전체 조회 성공")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<NoticeListResponse>> getNotices(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return noticeService.getNotices(keyword, page, size);
    }


    @Operation(
            summary = "공지사항 상세 조회",
            description = "특정 공지사항의 상세 내용을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 상세 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "공지사항이 존재하지 않는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"공지사항을 찾을 수 없습니다.\", \"data\": null}")
                            )
                    )
            }
    )
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(@PathVariable Long noticeId) {
        return noticeService.getNotice(noticeId);
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
        return noticeService.updateNotice(token, noticeId, noticeDTO);
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
        return noticeService.deleteNotice(token, noticeId);
    }
}
