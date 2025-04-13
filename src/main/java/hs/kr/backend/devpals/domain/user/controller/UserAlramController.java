package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.AlramDTO;
import hs.kr.backend.devpals.domain.user.service.UserAlramService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAlramController {

    private final UserAlramService userAlramService;

    @GetMapping("/alram")
    @Operation(summary = "알림 가져오기", description = "알림을 가져옵니다(해당 api 구현된것이 아닙니다 테스트용도로 만들었어요!)")
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
    public ResponseEntity<ApiResponse<List<AlramDTO>>> getUserAlram(@RequestHeader("Authorization") String token,
                                                                    @RequestParam(required = false) String filter) {

        return userAlramService.getUserAlram(token, filter);
    }
}
