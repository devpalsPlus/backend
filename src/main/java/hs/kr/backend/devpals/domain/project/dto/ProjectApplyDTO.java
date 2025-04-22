package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "프로젝트 지원 DTO")
public class ProjectApplyDTO {

    @Schema(description = "지원자의 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "지원자의 전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "지원 메시지 또는 자기소개", example = "열심히 참여하겠습니다!")
    private String message;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "지원자의 스킬 목록 (응답용)", accessMode = Schema.AccessMode.READ_ONLY)
    private List<SkillTagResponse> skills;

    @Schema(description = "지원자의 커리어 목록")
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
