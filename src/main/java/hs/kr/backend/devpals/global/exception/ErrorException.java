package hs.kr.backend.devpals.global.exception;

import lombok.Getter;

@Getter
public enum ErrorException {
    INVALID_PASSWORD("잘못된 비밀번호입니다.", 401),
    SERVER_ERROR("서버 오류가 발생했습니다.", 500),
    UNAUTHORIZED("인증 권한이 없습니다.", 403), // 인증 권한 없음
    TOKEN_EXPIRED("토큰이 만료되었습니다.", 401), // 토큰 만료
    FORBIDDEN("접근이 금지되었습니다.", 403), // 접근이 금지됨
    USER_ID_NOT_FOUND("해당 유저가 토큰에 존재하지 않습니다.", 400),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", 404);// 멤버 아이디가 없는 경우

    private final String message;
    private final int statusCode;

    ErrorException(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

}