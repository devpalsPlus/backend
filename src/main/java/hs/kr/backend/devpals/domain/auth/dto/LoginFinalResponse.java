package hs.kr.backend.devpals.domain.auth.dto;

import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginFinalResponse {
    private TokenDataResponse data;
    private LoginUserResponse user;
}
