package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserApplicantResponse {
    Long id;
    String nickname;

    public static UserApplicantResponse fromEntity(UserEntity user) {
        return UserApplicantResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}
