package hs.kr.backend.devpals.domain.auth.dto;

import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import lombok.Getter;

@Getter
public class LoginCustomResponse<T> extends ApiCustomResponse<T> {
    private Object user;

    public LoginCustomResponse(boolean success, String message, T data, Object user) {
        super(success, message, data);
        this.user = user;
    }
}