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
    @Operation(summary = "알림 가져오기", description = "알림을 가져옵니다. 데이터가 존재하지 않을경우 아래와 같이 값을 전달합니다 {\n" +
            "    \"success\": true,\n" +
            "    \"message\": \"알림이 존재하지 않습니다.\",\n" +
            "    \"data\": null\n" +
            "}")
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
    public ResponseEntity<ApiResponse<List<AlarmDto>>> getUserAlarm(@RequestHeader("Authorization") String token,
                                                                    @RequestParam(required = false,defaultValue = "0") Integer filter) {

        return userAlarmService.getUserAlarm(token, filter);
    }

    @PutMapping("/alarm")
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
    public SseEmitter connect(@RequestHeader("Authorization") String token) {
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
