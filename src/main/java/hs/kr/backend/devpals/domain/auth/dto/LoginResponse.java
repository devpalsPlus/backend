package hs.kr.backend.devpals.domain.auth.dto;

import hs.kr.backend.devpals.domain.user.dto.LoginUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    //private String refreshToken;
}