package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.tag.service.TagService;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@Schema(name = "ProjectApplicantResultResponse", description = "프로젝트 지원자 결과 응답(상태별 분리)")
public class ProjectApplicantResultResponse {

    @ArraySchema(
            schema = @Schema(implementation = ProjectApplicantResultDto.class),
            arraySchema = @Schema(description = "수락(ACCEPTED)된 지원자 목록")
    )
    private List<ProjectApplicantResultDto> accepted;

    @ArraySchema(
            schema = @Schema(implementation = ProjectApplicantResultDto.class),
            arraySchema = @Schema(description = "거절(REJECTED)된 지원자 목록")
    )
    private List<ProjectApplicantResultDto> rejected;

    public static ProjectApplicantResultResponse fromEntity(List<ApplicantEntity> applicants, TagService tagService) {
        Map<Boolean, List<ProjectApplicantResultDto>> partitioned = applicants.stream()
                .map(applicant -> ProjectApplicantResultDto.fromEntity(applicant, tagService))
                .collect(Collectors.partitioningBy(dto -> dto.getStatus().equals(ApplicantStatus.ACCEPTED)));

        return ProjectApplicantResultResponse.builder()
                .accepted(partitioned.get(true))
                .rejected(partitioned.get(false).stream()
                        .filter(dto -> dto.getStatus().equals(ApplicantStatus.REJECTED))
                        .collect(Collectors.toList()))
                .build();
    }
}
