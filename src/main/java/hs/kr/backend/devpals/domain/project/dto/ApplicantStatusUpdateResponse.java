package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicantStatusUpdateResponse {

    private Long id;
    private Long userId;
    private Long projectId;
    private String message;
    private String email;
    private String phoneNumber;
    private List<CareerDto> career;
    private String status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;


    public static ApplicantStatusUpdateResponse fromEntity(ApplicantEntity applicant){
        return ApplicantStatusUpdateResponse.builder()
                .id(applicant.getId())
                .userId(applicant.getUser().getId())
                .projectId(applicant.getProject().getId())
                .message(applicant.getMessage())
                .email(applicant.getEmail())
                .phoneNumber(applicant.getPhoneNumber())
                .career(applicant.getCareer())
                .status(applicant.getStatus().toString())
                .createAt(applicant.getCreatedAt())
                .updateAt(applicant.getUpdatedAt())
                .build();
    }
}
