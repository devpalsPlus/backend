package hs.kr.backend.devpals.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String email; // request 하나인건 param으로 바꾸는게 좋지 않나..?
}
