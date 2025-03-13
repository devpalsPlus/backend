package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectApplicantResultResponse {

    private List<ProjectApplicantResultDto> accepted;
    private List<ProjectApplicantResultDto> rejected;

    public static ProjectApplicantResultResponse fromEntity(List<ApplicantEntity> applicants) {
        Map<Boolean, List<ProjectApplicantResultDto>> partitioned = applicants.stream()
                .map(ProjectApplicantResultDto::fromEntity)
                .collect(Collectors.partitioningBy(dto ->
                        dto.getStatus().equals(ApplicantStatus.ACCEPTED)));

        return ProjectApplicantResultResponse.builder()
                .accepted(partitioned.get(true))
                .rejected(partitioned.get(false).stream()
                        .filter(dto -> dto.getStatus().equals(ApplicantStatus.REJECTED))
                        .collect(Collectors.toList()))
                .build();
    }

//    public static ProjectApplicantResultResponse fromEntity(List<ApplicantEntity> applicants) {
//        List<ProjectApplicantResultDto> projectApplicantResultsAccepted = new ArrayList<>();
//        List<ProjectApplicantResultDto> projectApplicantResultsRejected = new ArrayList<>();
//        applicants.stream()
//                .map(ProjectApplicantResultDto::fromEntity)
//                .forEach(dto -> {
//                    if (dto.getStatus().equals(ApplicantStatus.ACCEPTED)) { // dto 클래스의 a 값을 확인하는 메서드 (getter)
//                        projectApplicantResultsAccepted.add(dto);
//                    } else if (dto.getStatus().equals(ApplicantStatus.REJECTED)){
//                        projectApplicantResultsRejected.add(dto);
//                    }
//                });
//
//        return ProjectApplicantResultResponse.builder()
//                .accepted(projectApplicantResultsAccepted)
//                .rejected(projectApplicantResultsRejected)
//                .build();
//    }

}
