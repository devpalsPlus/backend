package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMineResponse;
import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.dto.UserUpdateRequest;
import hs.kr.backend.devpals.domain.user.service.UserService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "유저 관련 API")
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

    @PostMapping("/profile-img")
    public ResponseEntity<ApiResponse<String>> updateProfileImg(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file){
        return userService.updateProfileImage(token, file);
    }

    @GetMapping("/project")
    public ResponseEntity<ApiResponse<List<ProjectMineResponse>>> getMyProjects(@RequestHeader("Authorization") String token) {
        return userService.getMyProject(token);
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ProjectApplyResponse>>> getMyProjectList(@RequestHeader("Authorization")  String token){
        return userService.getMyProjectApply(token);
    }

    @GetMapping("/{id}/project")
    public ResponseEntity<ApiResponse<List<ProjectMineResponse>>> getUserProjects(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return userService.getUserProject(token, id);
    }
}
