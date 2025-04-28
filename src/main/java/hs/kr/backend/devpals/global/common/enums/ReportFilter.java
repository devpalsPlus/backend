package hs.kr.backend.devpals.global.common.enums;

import lombok.Getter;


public enum ReportFilter {
    USER(1),PROJECT(2),COMMENT(3),RECOMMENT(4),INQUIRY(5);

    private final Integer value;

    ReportFilter( Integer value) {
        this.value = value;
    }
    public Integer getValue(){
        return this.value;
    }

    public static ReportFilter fromValue(Integer value) {
        for (ReportFilter filter : ReportFilter.values()) {
            if (filter.value.equals(value)) {
                return filter;
            }
        }
        throw new IllegalArgumentException("Unknown ReportFilter value: " + value);
    }
}
