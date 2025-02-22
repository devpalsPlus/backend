package hs.kr.backend.devpals.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorException errorException;
    private final String code;
    private final int statusCode;

    public CustomException(ErrorException errorException) {
        super(errorException.getMessage());
        this.errorException = errorException;
        this.code = errorException.name();
        this.statusCode = errorException.getStatusCode();
    }

    public CustomException(ErrorException errorException, String code, String message) {
        super(message);
        this.errorException = errorException;
        this.code = code;
        this.statusCode = errorException.getStatusCode();
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "code='" + code + '\'' +
                ", message='" + getMessage() + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
