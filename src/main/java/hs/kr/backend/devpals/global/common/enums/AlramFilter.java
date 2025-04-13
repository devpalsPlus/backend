package hs.kr.backend.devpals.global.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum AlramFilter {
    ALL("전체"),
    APPLIED_PROJECTS("지원한 프로젝트"),
    APPLICANT_CHECK("지원자 확인"),
    COMMENT_AND_REPLY("댓글&답변");

    private final String displayName;

    AlramFilter(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static AlramFilter fromDisplayName(String value) {
        return Stream.of(AlramFilter.values())
                .filter(filter -> filter.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid filter name: " + value));
    }
}