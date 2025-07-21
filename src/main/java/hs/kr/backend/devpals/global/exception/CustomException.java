package hs.kr.backend.devpals.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final int code;
    private final String message;

    public CustomException(ErrorException errorException) {
        super(errorException.getMessage());
        this.code = errorException.getCode();
        this.message = errorException.getMessage();
    }

    public CustomException(ErrorException errorException, String customMessage) {
        super(customMessage);
        this.code = errorException.getCode();
        this.message = customMessage;
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "message='" + message + '\'' +
                ", code=" + code +
                '}';
    }
}
