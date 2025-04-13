package hs.kr.backend.devpals.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final String message;

    public CustomException(ErrorException errorException) {
        super(errorException.getMessage());
        this.message = errorException.getMessage();
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "message='" + message + '\'' +
                '}';
    }
}
