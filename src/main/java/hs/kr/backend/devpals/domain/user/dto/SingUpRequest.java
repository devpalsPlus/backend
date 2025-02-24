package hs.kr.backend.devpals.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingUpRequest {
    String email;
    String password;
    String nickname;
}
