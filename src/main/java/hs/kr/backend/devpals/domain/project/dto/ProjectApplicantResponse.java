package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.domain.user.dto.UserApplicantResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProjectApplicantResponse {

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
    private UserApplicantResponse user;

    public static ProjectApplicantResponse fromEntity(ApplicantEntity applicant){
        return ProjectApplicantResponse.builder()
                .id(applicant.getId())
                .userId(applicant.getUser().getId())
                .projectId(applicant.getProject().getId())
                .message(applicant.getMessage())
                .email(applicant.getEmail())
                .phoneNumber(applicant.getPhoneNumber())
                .career(applicant.getCareer()) // 공고 지원자 상세보기 반환 내용 참조
                .status(applicant.getStatus())
                .createAt(applicant.getCreatedAt())
                .updatedAt(applicant.getUpdatedAt())
                .user(UserApplicantResponse.fromEntity(applicant.getUser()))
                .build();
    }


//   public static ProjectApplicantResponse fromEntity(ApplicantEntity applicant, UserEntity user, ProjectEntity project){
//        return ProjectApplicantResponse.builder()
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
