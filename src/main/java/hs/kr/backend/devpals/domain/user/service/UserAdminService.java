package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.tag.service.TagService;
import hs.kr.backend.devpals.domain.user.dto.AdminUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import hs.kr.backend.devpals.domain.user.dto.UserAdminPreviewResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final TagService tagService;


    public ResponseEntity<ApiResponse<List<UserAdminPreviewResponse>>> getAllUsersPreview() {
        List<UserEntity> users = userRepository.findAll();

        //boolean isOnline = userSessionManager.isOnline(user.getId());
        List<UserAdminPreviewResponse> result = users.stream()
                .map(UserAdminPreviewResponse::from)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "회원 미리보기 조회 성공", result));
    }

    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsersDetail() {
        List<UserEntity> users = userRepository.findAll();

        List<AdminUserResponse> result = users.stream()
                .map(user -> AdminUserResponse.fromEntity(user, tagService))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "회원 전체 상세 조회 성공", result));
    }
}
