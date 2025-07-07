package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserAdminPreviewResponse {

    private Long id;
    private String nickname;
    private String email;
    private String profileImg;
    private Boolean isOnline;
    private LocalDateTime createdAt;

    public static UserAdminPreviewResponse from(UserEntity user, boolean isOnline) {
        return UserAdminPreviewResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .isOnline(isOnline)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
