package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.AlarmDto;
import hs.kr.backend.devpals.domain.user.dto.AlarmRequest;
import hs.kr.backend.devpals.domain.user.service.AlarmService;
import hs.kr.backend.devpals.domain.user.service.UserAlarmService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAlarmController {

    private final UserAlarmService userAlarmService;
    private final AlarmService alarmService;

    @GetMapping("/alarm")
    @Operation(summary = "알림 가져오기", description = "알람 데이터는 기본 알람(AlarmDto)과 댓글 알람(CommentAlarmDto) 두 가지 타입으로 응답될 수 있습니다.\"\n" +
            ")")

    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 가져오기 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "기본 알람 응답",
                                    value = "{\"success\": true, \"message\": \"알림을 가져왔습니다.\", \"data\": [{\"id\": 1, \"routingId\": 1, \"content\": \"기본 알림 내용\", \"enabled\": true, \"alarmFilterId\": 1, \"createdAt\": \"2025-04-25T10:00:00\"}]}"
                            ),
                            @ExampleObject(
                                    name = "댓글 알람 응답",
                                    value = "{\"success\": true, \"message\": \"알림을 가져왔습니다.\", \"data\": [{\"id\": 2, \"routingId\": 2, \"content\": \"댓글 알림 내용\", \"enabled\": true, \"alarmFilterId\": 2, \"createdAt\": \"2025-04-25T11:00:00\", \"replier\": 1, \"reCommentUserId\": 2}]}",
                                    description = "replier 값은 Integer로 설정되어 있습니다. " +
                                            "전체: 0 ,댓글: 1, 댓글 답글: 2, 문의 답글(현재 미구현): 3, 신고 답글(현재 미구현): 4"
                            )
                    }
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "알림 가져오기 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"알림을 가져오던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<List<AlarmDto>>> getUserAlarm(@RequestHeader("Authorization") String token,
                                                                    @RequestParam(required = false,defaultValue = "0") Integer filter) {

        return userAlarmService.getUserAlarm(token, filter);
    }

    @PatchMapping("/alarm")
    @Operation(summary = "알림 수정하기", description = "알림 enable을 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 수정하기 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "알림 수정하기 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"알림을 수정하던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<AlarmDto>> putUserAlarm(@RequestHeader("Authorization") String token,
                                                                    @RequestBody AlarmRequest alarmRequest) {

        return userAlarmService.putAlarm(token, alarmRequest);
    }

    @DeleteMapping("/alarm/{alarmId}")
    @Operation(summary = "알림 삭제하기", description = "알림을 삭제합니다(지원한 프로젝트 (alarmFilterId: 1)는 삭제 불가)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 가져오기 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "알림 가져오기 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"알림을 가져오던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> deleteUserAlarm(@RequestHeader("Authorization") String token,@PathVariable Long alarmId) {

        return userAlarmService.deleteAlarm(token, alarmId);
    }

    // SSE 연결 엔드포인트
    @Operation(summary = "SSE 연결", description = "알림을 받기 위해 SSE연결을 진행합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 가져오기 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "알림 가져오기 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"알림을 가져오던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam("accessToken") String token) {
        return alarmService.createEmitter(token);
    }

    @GetMapping("/send-alarm")
    @Operation(summary = "알림 보내기", description = "현재 로그인한 유저에게 알람을 전송합니다. alarmFilter는 현재 0,1,2,3이 있으며, 0은 전체, 1은 지원한 프로젝트, 2는 지원자 확인, 3은 댓글 & 답변입니다. ")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 가져오기 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "알림 가져오기 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"알림을 가져오던 중 오류가 발생했습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiResponse<String>> sendAlarm(@RequestHeader("Authorization") String token,@RequestParam("alarmFilter")Integer alarmFilter) {

        return alarmService.sendAlarmTest(token,alarmFilter);
    }
}
