package hs.kr.backend.devpals.global.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ReplierFilter {

    ALL("전체",0),
    COMMENT("댓글",1),
    RECOMMENT("대댓글",2),
    INQUIRY("문의 답글",3),
    REPORT("신고 답글",4);

    private final String displayName;
    private final Integer value;

    private static final Map<Integer, ReplierFilter> VALUE_MAP =
            Arrays.stream(ReplierFilter.values())
                    .collect(Collectors.toMap(ReplierFilter::getValue, f -> f));

    ReplierFilter(String displayName, Integer value) {
        this.displayName = displayName;
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }

    @JsonCreator
    public static ReplierFilter fromDisplayName(String value) {
        return Stream.of(ReplierFilter.values())
                .filter(filter -> filter.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid filter name: " + value));
    }

    public static Optional<ReplierFilter> fromValue(Integer value) {
        return Optional.ofNullable(VALUE_MAP.get(value));
    }

    public static boolean isValid(Integer value) {
        return VALUE_MAP.containsKey(value);
    }
}
