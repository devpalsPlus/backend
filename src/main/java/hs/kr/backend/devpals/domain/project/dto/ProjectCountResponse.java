package hs.kr.backend.devpals.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectCountResponse {
    private long ongoingProjectCount;
    private long totalProjectCount;
    private long endProjectCount;
}
