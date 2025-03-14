package hs.kr.backend.devpals.domain.user.controller;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMineResponse;
import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.dto.UserUpdateRequest;
import hs.kr.backend.devpals.domain.user.service.UserService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping()
    @Operation(summary = "본인 정보 조회", description = "본인의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "유저 정보 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"유저 정보를 조회할 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<UserResponse>> getUser(@RequestHeader("Authorization") String token) {
        return userService.getUserInfo(token);
    }

    @GetMapping("/{id}")
    @Operation(summary = "상대방 정보 조회", description = "상대방의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상대방 정보 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "상대방 정보 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<UserResponse>> getUserById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return userService.getUserInfoById(token, id);
    }

    @PutMapping()
    @Operation(summary = "본인 정보 업데이트", description = "본인의 정보를 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "유저 정보 업데이트 성공")
    @ApiResponse(
            responseCode = "400",
            description = "유저 정보 업데이트 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"유저 정보를 업데이트할 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<UserResponse>> updateUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserUpdateRequest request) {
        return userService.userUpdateInfo(token, request);
    }

    @PostMapping("/profile-img")
    @Operation(summary = "본인 정보 이미지 업데이트", description = "본인의 정보 중 이미지만 업데이트 합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 이미지 업데이트 성공")
    @ApiResponse(
            responseCode = "400",
            description = "프로필 이미지 업데이트 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"이미지 파일 형식이 올바르지 않습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<String>> updateProfileImg(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file){
        return userService.updateProfileImage(token, file);
    }

    @GetMapping("/project")
    @Operation(summary = "본인이 참여한 프로젝트 조회", description = "본인이 참여한 프로젝트의 조회합니다.")
    @ApiResponse(responseCode = "200", description = "본인 참여 프로젝트 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "본인 참여 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"참여한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectMineResponse>>> getMyProjects(@RequestHeader("Authorization") String token) {
        return userService.getMyProject(token);
    }

    @GetMapping("/applications")
    @Operation(summary = "본인이 지원한 프로젝트 조회", description = "본인이 지원한 프로젝트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "본인 지원 프로젝트 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "본인 지원 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"지원한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectApplyResponse>>> getMyProjectList(@RequestHeader("Authorization") String token){
        return userService.getMyProjectApply(token);
    }

    @GetMapping("/{id}/project")
    @Operation(summary = "상대방이 참여한 프로젝트 조회", description = "상대방이 참여한 프로젝트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상대방 참여 프로젝트 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "상대방 참여 프로젝트 조회 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiCustomResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\": \"해당 유저가 참여한 프로젝트를 찾을 수 없습니다.\", \"data\": null}")
            )
    )
    public ResponseEntity<ApiCustomResponse<List<ProjectMineResponse>>> getUserProjects(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        return userService.getUserProject(token, id);
    }
}
