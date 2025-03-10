package hs.kr.backend.devpals.domain.project.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.domain.project.dto.ProjectAllDto;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import jakarta.persistence.*;
import lombok.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isBeginner;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDone;

    @Column(nullable = false)
    private LocalDate recruitmentStartDate;

    @Column(nullable = false)
    private LocalDate recruitmentEndDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String positionTags;  // JSON 형태로 저장

    @Column(nullable = false, columnDefinition = "TEXT")
    private String skillTags;  // JSON 형태로 저장

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private int views;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 📌 프로젝트 빌더 메소드 (Skill을 `List<SkillTagResponse>` 형태로 받음)
     */
    public static ProjectEntity fromRequest(ProjectAllDto request, List<String> positionNames, List<SkillTagResponse> skillResponses) {
        return ProjectEntity.builder()
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .positionTags(convertListToJson(positionNames))
                .skillTags(convertSkillListToJson(skillResponses))
                .build();
    }

    /**
     * 📌 프로젝트 업데이트 메소드 (Skill을 `List<SkillTagResponse>`로 저장)
     */
    public void updateProject(ProjectAllDto request, List<String> positionNames, List<SkillTagResponse> skillResponses) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.totalMember = request.getTotalMember();
        this.startDate = request.getStartDate();
        this.estimatedPeriod = request.getEstimatedPeriod();
        this.method = request.getMethodType();
        this.isBeginner = request.getIsBeginner();
        this.isDone = request.getIsDone();
        this.recruitmentStartDate = request.getRecruitmentStartDate();
        this.recruitmentEndDate = request.getRecruitmentEndDate();
        this.positionTags = convertListToJson(positionNames);
        this.skillTags = convertSkillListToJson(skillResponses);
    }

    /**
     * 📌 Getter: JSON → List<String> 변환
     */
    public List<String> getPositionTagsAsList() {
        return convertJsonToList(this.positionTags);
    }

    /**
     * 📌 Getter: JSON → List<SkillTagResponse> 변환
     */
    public List<SkillTagResponse> getSkillTagsAsList() {
        return convertJsonToSkillList(this.skillTags);
    }

    /**
     * 📌 Setter: List<String> → JSON 문자열 변환 후 저장
     */
    public void setPositionTags(List<String> positionTags) {
        this.positionTags = convertListToJson(positionTags);
    }

    /**
     * 📌 Setter: List<SkillTagResponse> → JSON 변환 후 저장
     */
    public void setSkillTags(List<SkillTagResponse> skillTags) {
        this.skillTags = convertSkillListToJson(skillTags);
    }

    /**
     * 📌 JSON 변환 메서드 (List<String> → JSON)
     */
    private static String convertListToJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (IOException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

    /**
     * 📌 JSON 변환 메서드 (List<SkillTagResponse> → JSON)
     */
    private static String convertSkillListToJson(List<SkillTagResponse> skills) {
        try {
            return objectMapper.writeValueAsString(skills);
        } catch (IOException e) {
            throw new RuntimeException("SkillTagResponse JSON 변환 오류: " + e.getMessage(), e);
        }
    }

    /**
     * 📌 JSON 변환 메서드 (JSON → List<String>)
     */
    private static List<String> convertJsonToList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

    /**
     * 📌 JSON 변환 메서드 (JSON → List<SkillTagResponse>)
     */
    private static List<SkillTagResponse> convertJsonToSkillList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<SkillTagResponse>>() {
            });
        } catch (IOException e) {
            try {
                // JSON이 `List<String>` 형태로 저장된 경우 변환 처리
                List<String> skillNames = objectMapper.readValue(json, new TypeReference<List<String>>() {
                });
                return skillNames.stream()
                        .map(skillName -> new SkillTagResponse(skillName, "default-img.png")) //  기본 이미지 URL 설정
                        .collect(Collectors.toList());
            } catch (IOException ex) {
                throw new RuntimeException("SkillTagResponse JSON 역직렬화 오류: " + json, ex);
            }
        }
    }
}
