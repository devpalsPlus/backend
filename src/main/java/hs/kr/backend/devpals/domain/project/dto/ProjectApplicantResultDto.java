package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.domain.user.dto.UserApplicantResultResponse;
import hs.kr.backend.devpals.domain.tag.service.TagService;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProjectApplicantResultDto {

    private Long id;
    private Long userId;
    private Long projectId;
    private String message;
    private String email;
    private String phoneNumber;
    private List<CareerDto> career;
    private ApplicantStatus status;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
    private UserApplicantResultResponse user;

    public static ProjectApplicantResultDto fromEntity(ApplicantEntity applicant, TagService userfacade) {
        return ProjectApplicantResultDto.builder()
                .id(applicant.getId())
                .userId(applicant.getUser().getId())
                .projectId(applicant.getProject().getId())
                .message(applicant.getMessage())
                .email(applicant.getEmail())
                .phoneNumber(applicant.getPhoneNumber())
                .career(applicant.getCareer())
                .status(applicant.getStatus())
                .createAt(applicant.getCreatedAt())
                .updatedAt(applicant.getUpdatedAt())
                .user(UserApplicantResultResponse.fromEntity(applicant.getUser(), userfacade))
                .build();
    }

//    public static ProjectApplicantResultResponse fromEntity(ApplicantEntity applicant, ProjectEntity project, UserEntity user) {
//        return ProjectApplicantResultResponse.builder()
//                .id(applicant.getId())
//                .userId(user.getId())
//                .projectId(project.getId())
//                .message(applicant.getMessage())
//                .email(applicant.getEmail())
//                .phoneNumber(applicant.getPhoneNumber())
//                .career(applicant.getCareer())
//                .status(applicant.getStatus())
//                .createAt(applicant.getCreatedAt())
//                .updatedAt(applicant.getUpdatedAt())
//                .user(UserApplicantResponse.fromEntity(user))
//                .build();
//    }
}
