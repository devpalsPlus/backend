package hs.kr.backend.devpals.domain.notice.dto;

import hs.kr.backend.devpals.domain.notice.entity.NoticeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class NoticeListResponse {

    @Schema(description = "공지사항 목록")
    private List<NoticeDTO> notices;

    @Schema(description = "전체 페이지 수", example = "3")
    private int totalPages;

    public static NoticeListResponse from(List<NoticeEntity> entities, int totalPages) {
        List<NoticeDTO> dtoList = entities.stream()
                .map(NoticeDTO::fromEntity)
                .collect(Collectors.toList());

        return NoticeListResponse.builder()
                .notices(dtoList)
                .totalPages(totalPages)
                .build();
    }
}