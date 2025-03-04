package hs.kr.backend.devpals.global.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import lombok.Getter;

@Getter
public enum MethodType {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    ON_OFFLINE("온/오프라인");

    private final String value;

    MethodType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static MethodType from(String value) {
        for (MethodType type : MethodType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new CustomException(ErrorException.INVALID_METHOD_TYPE);
    }
}
