package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserApplicantResultResponse {

    private Long id;
    private String nickname;
    private String email;
    private String bio;
    private String profileImg;
    private List<UserSkillTagResponse> userSkillTags;


    public static UserApplicantResultResponse fromEntity(UserEntity user, UserFacade userFacade) {
        List<Long> skillIds = user.getSkillIds();

        List<UserSkillTagResponse> skillResponses = userFacade.getSkillTagsByIds(skillIds).stream()
                .map(UserSkillTagResponse::fromEntity)
                .collect(Collectors.toList());

        return UserApplicantResultResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImg(user.getProfileImg())
                .userSkillTags(skillResponses)
                .build();
    }
}
