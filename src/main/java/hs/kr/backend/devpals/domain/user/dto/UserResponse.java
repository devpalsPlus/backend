package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.UserLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String nickname;
    private String email;
    private String bio;
    private String profileImg;
    private UserLevel userLevel;
    private String github;
    private String positionTag;
    private List<SkillTagResponse> skills;
    private List<CareerDto> career;
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
                .positionTag(user.getPositionTag() != null ? user.getPositionTag().getName() : "null")
                .career(user.getCareer())
                .skills(user.getSkills().stream()
                        .map(SkillTagResponse::fromEntity) // DTO로 변환하여 리스트 반환
                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
