package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.dto.UserUpdateRequest;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.service.UserService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@RequestHeader("Authorization") String token) {
        return userService.getUserInfo(token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return userService.getUserInfoById(token, id);
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserUpdateRequest request) {
        return userService.userUpdateInfo(token, request);
    }
}
