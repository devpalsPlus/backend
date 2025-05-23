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
    private boolean isAdmin;
    private boolean hasRequiredTags;

    public static LoginUserResponse fromEntity(UserEntity user) {
        boolean hasRequiredTags =
                user.getSkillIds() != null && !user.getSkillIds().isEmpty()
                        && user.getPositionIds() != null && !user.getPositionIds().isEmpty();

        return new LoginUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getIsAdmin(),
                hasRequiredTags
        );
    }
}
