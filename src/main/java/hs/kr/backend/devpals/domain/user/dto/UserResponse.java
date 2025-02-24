package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.enums.UserLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String nickname;
    private String email;
    private String bio;
    private String profileImg;
    private UserLevel userLevel;
    private String github;
    private String career;
    private LocalDateTime createdAt;



    public static UserResponse fromEntity(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImg(user.getProfileImg())
                .userLevel(user.getUserLevel())
                .github(user.getGithub())
                .career(user.getCareer())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
