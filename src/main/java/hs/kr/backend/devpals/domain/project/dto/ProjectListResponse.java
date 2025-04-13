package hs.kr.backend.devpals.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListResponse {
    private int currentPage;
    private int lastPage;
    private int total;
    private List<ProjectAllDto> projects;
}
