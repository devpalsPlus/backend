package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectUserResponse {
    private Long id;
    private String nickname;
    private String img;

    public static ProjectUserResponse fromEntity(UserEntity user) {
        return new ProjectUserResponse(user.getId(), user.getNickname(), user.getProfileImg());
    }
}
