package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserApplicantResultResponse {

    private Long id;
    private String nickname;
    private String email;
    private String bio;
    private String profileImg;
    private List<UserSkillTagResponse> userSkillTags;


    public static UserApplicantResultResponse fromEntity(UserEntity user) {
        return UserApplicantResultResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImg(user.getProfileImg())
                .userSkillTags(user.getSkills().stream().map(UserSkillTagResponse::fromEntity).toList())
                .build();
    }
}
