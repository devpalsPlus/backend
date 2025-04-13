package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUserResponse {
    private Long id;
    private String email;
    private String nickname;

    public static LoginUserResponse fromEntity(UserEntity user) {
        return new LoginUserResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
