package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.tag.service.TagService;
import hs.kr.backend.devpals.domain.user.dto.AdminUserListResponse;
import hs.kr.backend.devpals.domain.user.dto.AdminUserResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import hs.kr.backend.devpals.domain.user.dto.UserAdminPreviewResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final TagService tagService;
    private final FaqAdminService faqAdminService;


    public ResponseEntity<ApiResponse<List<UserAdminPreviewResponse>>> getAllUsersPreview(String token) {
        faqAdminService.validateAdmin(token);

        List<UserEntity> users = userRepository.findAll();

        //boolean isOnline = userSessionManager.isOnline(user.getId());
        List<UserAdminPreviewResponse> result = users.stream()
                .map(UserAdminPreviewResponse::from)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "회원 미리보기 조회 성공", result));
    }

    public ResponseEntity<ApiResponse<AdminUserListResponse>> getAllUsersDetail(int page, int size, String keyword, String token) {
        faqAdminService.validateAdmin(token);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserEntity> userPage;

        if (keyword != null && !keyword.isBlank()) {
            userPage = userRepository.findByNicknameContaining(keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<AdminUserResponse> result = userPage.getContent().stream()
                .map(user -> AdminUserResponse.fromEntity(user, tagService))
                .toList();

        AdminUserListResponse response = new AdminUserListResponse(result, userPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(200, true, "회원 전체 상세 조회 성공", response));
    }
}
