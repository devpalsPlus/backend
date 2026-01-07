package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.domain.user.dto.UserApplicantResultResponse;
import hs.kr.backend.devpals.domain.tag.service.TagService;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(name = "ProjectApplicantResultDto", description = "프로젝트 지원자 결과용 단건 DTO")
public class ProjectApplicantResultDto {

    @Schema(description = "지원(신청) ID", example = "101")
    private Long id;

    @Schema(description = "지원자 유저 ID", example = "42")
    private Long userId;

    @Schema(description = "프로젝트 ID", example = "7")
    private Long projectId;

    @Schema(description = "지원 메시지", example = "열심히 참여하겠습니다.")
    private String message;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @ArraySchema(
            schema = @Schema(implementation = CareerDto.class),
            arraySchema = @Schema(description = "경력 목록")
    )
    private List<CareerDto> career;

    @Schema(description = "지원 상태", example = "ACCEPTED")
    private ApplicantStatus status;

    @Schema(description = "생성일시", example = "2026-01-05T18:30:00")
    private LocalDateTime createAt;

    @Schema(description = "수정일시", example = "2026-01-05T18:31:10")
    private LocalDateTime updatedAt;

    @Schema(description = "지원자 결과용 유저 정보(태그 포함)")
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
}
