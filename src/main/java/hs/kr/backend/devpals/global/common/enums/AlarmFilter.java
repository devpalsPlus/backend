package hs.kr.backend.devpals.global.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AlarmFilter {
    ALL("전체",0),
    APPLIED_PROJECTS("지원한 프로젝트",1),
    APPLICANT_CHECK("지원자 확인",2),
    COMMENT_AND_REPLY("댓글&답변",3),
    INQUIRY("문의",4),
    REPORT("신고",5);

    private final String displayName;
    private final Integer value;

    private static final Map<Integer, AlarmFilter> VALUE_MAP =
            Arrays.stream(AlarmFilter.values())
                    .collect(Collectors.toMap(AlarmFilter::getValue, f -> f));

    AlarmFilter(String displayName, Integer value) {
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
    public static AlarmFilter fromDisplayName(String value) {
        return Stream.of(AlarmFilter.values())
                .filter(filter -> filter.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid filter name: " + value));
    }

    public static Optional<AlarmFilter> fromValue(Integer value) {
        return Optional.ofNullable(VALUE_MAP.get(value));
    }

    public static boolean isValid(Integer value) {
        return VALUE_MAP.containsKey(value);
    }
}