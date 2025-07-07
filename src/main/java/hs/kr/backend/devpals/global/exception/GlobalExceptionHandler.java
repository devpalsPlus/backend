package hs.kr.backend.devpals.global.exception;

import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  CustomException 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getCode()) // 동적으로 상태 코드 설정
                .body(ApiResponse.failure(ex.getMessage(), ex.getCode()));
    }

    /**
     *  JSON 파싱 오류 처리 (잘못된 methodType 입력 시 예외)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = findRootCause(ex);

        if (cause instanceof CustomException customEx) {
            return ResponseEntity
                    .status(customEx.getCode())
                    .body(ApiResponse.failure(customEx.getMessage(), customEx.getCode()));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("잘못된 JSON 형식입니다.", HttpStatus.BAD_REQUEST.value()));
    }

    /**
     *  기타 예외 처리 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("서버 내부 오류 발생 : ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
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


    /**
     *  필수 헤더 누락 처리 (예: Authorization 없음)
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(
                        "필수 요청 헤더가 누락되었습니다: " + ex.getHeaderName(),
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

}
