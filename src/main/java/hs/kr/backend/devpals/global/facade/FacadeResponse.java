package hs.kr.backend.devpals.global.facade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacadeResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object user;

    public FacadeResponse(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}