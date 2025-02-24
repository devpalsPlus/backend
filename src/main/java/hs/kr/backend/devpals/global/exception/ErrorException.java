package hs.kr.backend.devpals.global.exception;

import lombok.Getter;

@Getter
public enum ErrorException {
    INVALID_PASSWORD("비밀번호가 틀렸습니다."),
    SERVER_ERROR("서버 오류가 발생했습니다."),
    UNAUTHORIZED("인증 권한이 없습니다."),
    TOKEN_EXPIRED("토큰이 만료되었습니다."),
    FORBIDDEN("접근이 금지되었습니다."),
    USER_ID_NOT_FOUND("해당 유저가 토큰에 존재하지 않습니다."),
    USER_NOT_FOUND("가입되지 않은 계정입니다.");

    private final String message;

    ErrorException(String message) {
        this.message = message;
    }
}
