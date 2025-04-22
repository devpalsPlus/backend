package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAlarmDto {
    private Long userId;
    private String nickName;

    public static UserAlarmDto of(UserEntity user){
        return new UserAlarmDto(user.getId(), user.getNickname());
    }
}
