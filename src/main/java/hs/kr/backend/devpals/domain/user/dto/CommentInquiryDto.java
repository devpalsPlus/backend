package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryDto;
import hs.kr.backend.devpals.domain.project.dto.CommentDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentInquiryDto {

    private List<CommentDTO> comments;
    private List<InquiryDto> inquiries;

    public static CommentInquiryDto of(List<CommentDTO> comments, List<InquiryDto> inquiries) {
        return CommentInquiryDto.builder()
                .comments(comments)
                .inquiries(inquiries)
                .build();
    }
}
