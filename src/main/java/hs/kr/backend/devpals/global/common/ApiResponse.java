package hs.kr.backend.devpals.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "API 공통 응답 객체")
public class ApiResponse<T> {

    @Schema(description = "상태 코드", example = "401")
    private final int code;

    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "응답 메시지", example = "성공적으로 처리되었습니다.")
    private final String message;

    @Schema(description = "응답 데이터", nullable = true)
    private final T data;

    public ApiResponse(int code, boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> failure(String message, int code) {
        return new ApiResponse<>(code, false, message, null);
    }
}