package hs.kr.backend.devpals.global.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum AlramFilter {
    ALL("전체",0),
    APPLIED_PROJECTS("지원한 프로젝트",1),
    APPLICANT_CHECK("지원자 확인",2),
    COMMENT_AND_REPLY("댓글&답변",3);

    private final String displayName;
    private final Integer value;

    AlramFilter(String displayName,Integer value) {
        this.displayName = displayName;
        this.value = value;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
    @JsonValue
    public Integer getValue() {
        return value;
    }

    @JsonCreator
    public static AlramFilter fromDisplayName(String value) {
        return Stream.of(AlramFilter.values())
                .filter(filter -> filter.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid filter name: " + value));
    }
}