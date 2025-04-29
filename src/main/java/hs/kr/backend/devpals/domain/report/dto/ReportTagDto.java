package hs.kr.backend.devpals.domain.report.dto;


import lombok.Getter;

@Getter
public class ReportTagDto {

    private Long id;
    private String name;

    public ReportTagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
