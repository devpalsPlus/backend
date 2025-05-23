package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인한 사용자 정보 응답 DTO")
public class LoginUserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "개발자유저")
    private String nickname;

    @Schema(description = "관리자 여부", example = "false")
    private boolean isAdmin;

    @Schema(description = "작성자가 스킬과 포지션을 모두 1개 이상 가지고 있는지 여부", example = "true")
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
