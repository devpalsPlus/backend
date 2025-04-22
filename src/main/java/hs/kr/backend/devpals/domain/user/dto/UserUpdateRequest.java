package hs.kr.backend.devpals.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    private String nickname;
    private String bio;
    private String github;
    private Boolean beginner;
    private List<Long> positionTagIds;
    private List<Long> skillTagIds;
    private List<Map<String, Object>> career;
}
