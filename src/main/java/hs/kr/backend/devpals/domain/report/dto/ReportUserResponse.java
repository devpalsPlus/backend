package hs.kr.backend.devpals.domain.report.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportUserResponse {
    private Long userId;
    private String nickname;
    private String profileImg;

    public static ReportUserResponse fromEntity(UserEntity user) {
        return ReportUserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .build();
    }
}
