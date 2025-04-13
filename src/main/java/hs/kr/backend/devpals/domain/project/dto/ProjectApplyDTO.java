package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProjectApplyDTO {
    private String email;
    private String phoneNumber;
    private String message;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<SkillTagResponse> skills;

    private List<CareerDto> career;

    public static ProjectApplyDTO fromEntity(UserEntity user, ApplicantEntity applicant, List<SkillTagResponse> skills) {
        return ProjectApplyDTO.builder()
                .email(user.getEmail())
                .phoneNumber(applicant.getPhoneNumber())
                .message(applicant.getMessage())
                .skills(skills)
                .career(user.getCareer())
                .build();
    }
}
