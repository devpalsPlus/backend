package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserActivityOverviewResponse {
    private List<InquiryResponse> inquiries;
    private List<MyCommentResponse> comments;

    public static AdminUserActivityOverviewResponse of(List<InquiryResponse> inquiries, List<MyCommentResponse> comments) {
        return AdminUserActivityOverviewResponse.builder()
                .inquiries(inquiries)
                .comments(comments)
                .build();
    }
}