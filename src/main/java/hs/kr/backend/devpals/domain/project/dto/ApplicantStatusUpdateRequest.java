package hs.kr.backend.devpals.domain.project.dto;

import lombok.Getter;

@Getter
public class ApplicantStatusUpdateRequest {
    private Long applicantUserId;
    private String status;
}
