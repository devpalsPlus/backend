package hs.kr.backend.devpals.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    private String refreshToken; // request 하나인건 param으로 바꾸는게 좋지 않나..?
}
