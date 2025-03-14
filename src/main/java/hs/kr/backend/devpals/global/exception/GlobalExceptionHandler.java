package hs.kr.backend.devpals.global.exception;

import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  CustomException 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiCustomResponse<Object>> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCustomResponse.failure(ex.getMessage()));
    }

    /**
     *  JSON 파싱 오류 처리 (잘못된 methodType 입력 시 예외)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiCustomResponse<Object>> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = findRootCause(ex);

        if (cause instanceof CustomException customEx) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiCustomResponse.failure(customEx.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiCustomResponse.failure("잘못된 JSON 형식입니다."));
    }

    /**
     *  기타 예외 처리 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiCustomResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiCustomResponse.failure("서버 내부 오류가 발생했습니다."));
    }

    /**
     *  원인 예외를 찾아 반환 (중첩된 예외를 탐색)
     */
    private Throwable findRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
