package hs.kr.backend.devpals.domain.user.dto;


import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class UserUpdateRequest {
    private String nickname;
    private String bio;
    private String github;
    private Long positionTagId;
    private List<Long> skillTagIds;
    private List<Map<String, Object>> career;
}
