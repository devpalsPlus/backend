package hs.kr.backend.devpals.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminUserListResponse {
    private List<AdminUserResponse> users;
    private int totalPage;
}
