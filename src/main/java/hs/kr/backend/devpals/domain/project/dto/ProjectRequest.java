package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.global.common.enums.MethodType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProjectRequest {

    private String title;
    private String description;
    private int totalMember;
    private LocalDate startDate;
    private String estimatedPeriod;
    private MethodType methodType;
    private Boolean isBeginner;
    private Boolean isDone;
    private LocalDate recruitmentStartDate;
    private LocalDate recruitmentEndDate;
    private Long authorId;
    private List<Long> positionTagIds;
    private List<Long> skillTagIds;
}
