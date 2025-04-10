package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.AlramDTO;
import hs.kr.backend.devpals.domain.user.service.UserAlramService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
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
    public ResponseEntity<ApiResponse<List<AlramDTO>>> getUserAlram(@RequestHeader("Authorization") String token,
                                                                    @RequestParam(required = false) String filter) {

        return userAlramService.getUserAlram(token, filter);
    }
}
