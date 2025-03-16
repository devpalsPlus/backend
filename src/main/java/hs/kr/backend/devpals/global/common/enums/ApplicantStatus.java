package hs.kr.backend.devpals.global.common.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ApplicantStatus {
    WAITING, ACCEPTED, REJECTED;

    public static Optional<ApplicantStatus> fromString(String status) {
        return Arrays.stream(ApplicantStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst();
    }
}
