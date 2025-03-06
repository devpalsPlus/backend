package hs.kr.backend.devpals.domain.project.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.domain.project.dto.ProjectAllRequest;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import jakarta.persistence.*;
import lombok.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Projects")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int totalMember;

    @Column(nullable = false)
    private LocalDate startDate;

    private String estimatedPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MethodType method;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int views;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isBeginner;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDone;

    @Column(nullable = false)
    private LocalDate recruitmentStartDate;

    @Column(nullable = false)
    private LocalDate recruitmentEndDate;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    //
    @Column(columnDefinition = "JSON", nullable = false)
    private String positionTags;

    @Column(columnDefinition = "JSON", nullable = false)
    private String skillTags;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 프로젝트 빌더 메소드
    public static ProjectEntity fromRequest(ProjectAllRequest request, List<String> positionTags, List<String> skillTags) {
        ProjectEntity project = ProjectEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .totalMember(request.getTotalMember())
                .startDate(request.getStartDate())
                .estimatedPeriod(request.getEstimatedPeriod())
                .method(request.getMethodType())
                .authorId(request.getAuthorId())
                .isBeginner(request.getIsBeginner() != null ? request.getIsBeginner() : false)
                .isDone(request.getIsDone() != null ? request.getIsDone() : false)
                .recruitmentStartDate(request.getRecruitmentStartDate())
                .recruitmentEndDate(request.getRecruitmentEndDate())
                .views(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        project.setPositionTags(positionTags);
        project.setSkillTags(skillTags);

        return project;
    }

    // 프로젝트 업데이트
    public void updateProject(ProjectAllRequest request, List<String> positionTags, List<String> skillTags) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.totalMember = request.getTotalMember();
        this.startDate = request.getStartDate();
        this.estimatedPeriod = request.getEstimatedPeriod();
        this.method = request.getMethodType();
        this.isBeginner = request.getIsBeginner() != null ? request.getIsBeginner() : false;
        this.isDone = request.getIsDone() != null ? request.getIsDone() : false;
        this.recruitmentStartDate = request.getRecruitmentStartDate();
        this.recruitmentEndDate = request.getRecruitmentEndDate();
        this.updatedAt = LocalDateTime.now();
        setPositionTags(positionTags);
        setSkillTags(skillTags);
    }

    // Getter: JSON → List<String> 변환
    public List<String> getPositionTags() {
        try {
            return objectMapper.readValue(positionTags, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

    public List<String> getSkillTags() {
        try {
            return objectMapper.readValue(skillTags, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

    // Setter: List<String> → JSON 문자열 변환 후 저장
    public void setPositionTags(List<String> positionTags) {
        try {
            this.positionTags = objectMapper.writeValueAsString(positionTags);
        } catch (IOException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

    public void setSkillTags(List<String> skillTags) {
        try {
            this.skillTags = objectMapper.writeValueAsString(skillTags);
        } catch (IOException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

}

