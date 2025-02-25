package hs.kr.backend.devpals.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDataResponse {
    private String accessToken;
    private String refreshToken;
}