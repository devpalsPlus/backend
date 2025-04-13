package hs.kr.backend.devpals.domain.auth.dto;

import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.Getter;

@Getter
public class LoginResponse<T> extends ApiResponse<T> {
    private Object user;

    public LoginResponse(boolean success, String message, T data, Object user) {
        super(success, message, data);
        this.user = user;
    }
}