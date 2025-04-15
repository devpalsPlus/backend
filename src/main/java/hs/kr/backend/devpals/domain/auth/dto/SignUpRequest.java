package hs.kr.backend.devpals.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String password;
    private String nickname;
    private Boolean beginner;
}
