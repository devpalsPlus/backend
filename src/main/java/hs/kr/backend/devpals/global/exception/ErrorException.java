package hs.kr.backend.devpals.global.exception;

import lombok.Getter;

@Getter
public enum ErrorException {
    ACCESS_TOKEN_NOT_EXPIRED("액세스 토큰이 아직 만료되지 않았습니다."),
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다."),
    SERVER_ERROR("서버 오류가 발생했습니다."),
    UNAUTHORIZED("인증 권한이 없습니다."),
    TOKEN_EXPIRED("토큰이 만료되었습니다."),
    FORBIDDEN("접근이 금지되었습니다."),
    USER_ID_NOT_FOUND("해당 유저가 토큰에 존재하지 않습니다."),
    USER_NOT_FOUND("가입되지 않은 계정입니다."),
    DUPLICATE_EMAIL("중복된 이메일 입니다."),
    DUPLICATE_NICKNAME("중복된 닉네임 입니다."),
    EMAIL_SEND_FAILED("유효한 이메일을 입력해주세요."),
    EMAIL_VERTIFICATION("이메일 인증을 해주세요."),
    EMAIL_CODE_EXPIRED("이메일 코드가 만료되었습니다."),
    INVALID_CODE("이메일 인증코드를 다시 확인해주세요."),
    EMAIL_NOT_VERIFIED("이메일 인증코드를 입력해주세요.");

    private final String message;

    ErrorException(String message) {
        this.message = message;
    }
}
