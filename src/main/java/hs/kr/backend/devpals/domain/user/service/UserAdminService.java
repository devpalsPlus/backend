package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectAuthoredResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMyResponse;
import hs.kr.backend.devpals.domain.tag.service.TagService;
import hs.kr.backend.devpals.domain.user.dto.*;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final TagService tagService;
    private final FaqAdminService faqAdminService;
    private final UserProjectService userProjectService;
    private final UserProfileService userProfileService;
    private final RedisTemplate<String, String> redisTemplate;

    public ResponseEntity<ApiResponse<List<UserAdminPreviewResponse>>> getAllUsersPreview(String token) {
        faqAdminService.validateAdmin(token);

        List<UserEntity> users = userRepository.findAll();

        List<UserAdminPreviewResponse> result = users.stream()
                .map(user -> UserAdminPreviewResponse.from(user, isUserOnline(user.getId())))
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
                .map(user -> AdminUserResponse.fromEntity(user, tagService, isUserOnline(user.getId())))
                .toList();

        AdminUserListResponse response = new AdminUserListResponse(result, userPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(200, true, "회원 전체 상세 조회 성공", response));
    }

    public ResponseEntity<ApiResponse<AdminUserProjectOverviewResponse>> adminGetProjectOverview(String token, Long userId) {
        faqAdminService.validateAdmin(token);

        List<ProjectAuthoredResponse> authoredProjects = userProjectService.getMyProjectByAdmin(userId);
        List<ProjectMyResponse> ownedProjects = userProjectService.getOnlyCreatedProjectsByAdmin(userId);
        List<ProjectMyResponse> joinedProjects = userProjectService.getOnlyParticipatedProjectsByAdmin(userId);
        List<ProjectApplyResponse> appliedProjects = userProjectService.getMyProjectApplyByAdmin(userId);
        List<ProjectMyResponse> myJoinedProjects = userProjectService.getMyParticipatedProjectsByAdmin(userId);

        AdminUserProjectOverviewResponse response = AdminUserProjectOverviewResponse.of(
                authoredProjects,
                ownedProjects,
                joinedProjects,
                appliedProjects,
                myJoinedProjects
        );

        return ResponseEntity.ok(
                new ApiResponse<>(200, true, "회원의 프로젝트 지원,참여 등 조회 성공 (관리자용)", response)
        );
    }

    public ResponseEntity<ApiResponse<AdminUserActivityOverviewResponse>> adminGetUserActivity(String token, Long userId) {
        faqAdminService.validateAdmin(token);

        List<InquiryResponse> inquiries = userProfileService.getMyInquiriesByAdmin(userId);
        List<MyCommentResponse> comments = userProfileService.getMyCommentsByAdmin(userId);

        AdminUserActivityOverviewResponse response = AdminUserActivityOverviewResponse.builder()
                .inquiries(inquiries)
                .comments(comments)
                .build();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "회원 문의글/댓글 조회 성공 (관리자용)", response));
    }

    public ResponseEntity<ApiResponse<AdminVisitStatsResponse>> getVisitStats(String token) {
        faqAdminService.validateAdmin(token);

        Map<String, Long> daily = new TreeMap<>();
        Map<String, Long> weekly = new TreeMap<>();
        Map<String, Long> monthly = new TreeMap<>();

        for (int i = 0; i < 30; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            String key = "visit:" + date;
            String value = redisTemplate.opsForValue().get(key);
            long count = (value != null) ? Long.parseLong(value) : 0;

            daily.put(date.toString(), count);

            String weekKey = date.getYear() + "-W" + String.format("%02d", date.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
            weekly.merge(weekKey, count, Long::sum);

            String monthKey = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            monthly.merge(monthKey, count, Long::sum);
        }

        AdminVisitStatsResponse response = AdminVisitStatsResponse.from(daily, weekly, monthly);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "접속 통계 조회 성공", response));
    }

    private boolean isUserOnline(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("online:" + userId));
    }

}
