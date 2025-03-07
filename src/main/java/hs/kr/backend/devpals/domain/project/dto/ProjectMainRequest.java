package hs.kr.backend.devpals.domain.project.dto;

import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectMainRequest {
    private String title;
    private LocalDate recruitmentEndDate;
    private boolean isDone;
    private List<SkillTagResponse> skills;
    private List<String> positions;
    private String authorNickname;
    private int views;
}
