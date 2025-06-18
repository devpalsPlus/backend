package hs.kr.backend.devpals.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullApplicantInfoResponse {

    private List<ProjectApplicantResponse> applicants;
    private ProjectApplyDTO detail;
    private List<ProjectApplicantResultResponse> results;

    public static FullApplicantInfoResponse From(List<ProjectApplicantResponse> applicants,
                                                      ProjectApplyDTO detail,
                                                      List<ProjectApplicantResultResponse> results) {
        return FullApplicantInfoResponse.builder()
                .applicants(applicants)
                .detail(detail)
                .results(results)
                .build();
    }
}
