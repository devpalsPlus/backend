package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private Boolean beginner;
    private String github;
    private List<PositionTagResponse> positions;
    private List<SkillTagResponse> skills;
    private List<CareerDto> career;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(UserEntity user, UserFacade userFacade) {
        List<Long> positionIds = Optional.ofNullable(user.getPositionIds()).orElse(List.of());
        List<Long> skillIds = Optional.ofNullable(user.getSkillIds()).orElse(List.of());

        List<PositionTagResponse> positionResponses = userFacade.getPositionTagByIds(positionIds).stream()
                .map(PositionTagResponse::fromEntity)
                .collect(Collectors.toList());

        List<SkillTagResponse> skillResponses = userFacade.getSkillTagsByIds(skillIds).stream()
                .map(SkillTagResponse::fromEntity)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImg(user.getProfileImg())
                .beginner(user.getBeginner())
                .github(user.getGithub())
                .positions(positionResponses)
                .skills(skillResponses)
                .career(user.getCareer())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
