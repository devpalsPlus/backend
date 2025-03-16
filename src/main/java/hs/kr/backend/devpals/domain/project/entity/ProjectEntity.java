package hs.kr.backend.devpals.domain.project.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.domain.project.dto.ProjectAllDto;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import jakarta.persistence.*;
import lombok.*;

import javax.swing.text.Position;
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
    public static ProjectEntity fromRequest(ProjectAllDto request, List<PositionTagResponse> positionResponses, List<SkillTagResponse> skillResponses) {
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
                .positionTags(convertPositionListToJson(positionResponses))
                .skillTags(convertSkillListToJson(skillResponses))
                .build();
    }

    /**
     * 📌 프로젝트 업데이트 메소드 (Skill을 `List<SkillTagResponse>`로 저장)
     */
    public void updateProject(ProjectAllDto request, List<PositionTagResponse> positionNames, List<SkillTagResponse> skillResponses) {
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
        this.positionTags = convertPositionListToJson(positionNames);
        this.skillTags = convertSkillListToJson(skillResponses);
    }

    public void updateIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public List<PositionTagResponse> getPositionTagsAsList() {
        return convertJsonToPositionList(this.positionTags);
    }

    public List<SkillTagResponse> getSkillTagsAsList() {
        return convertJsonToSkillList(this.skillTags);
    }


    private static String convertSkillListToJson(List<SkillTagResponse> skills) {
        try {
            return objectMapper.writeValueAsString(skills);
        } catch (IOException e) {
            throw new RuntimeException("SkillTagResponse JSON 변환 오류: " + e.getMessage(), e);
        }
    }

    private static String convertPositionListToJson(List<PositionTagResponse> positions) {
        try {
            return objectMapper.writeValueAsString(
                    positions.stream()
                            .map(position -> new PositionTagResponse(position.getId(), position.getName()))
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            throw new RuntimeException("PositionTagResponse JSON 변환 오류", e);
        }
    }

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
                        .map(skillName -> new SkillTagResponse(null, skillName, "default-img.png")) //  기본 이미지 URL 설정
                        .collect(Collectors.toList());
            } catch (IOException ex) {
                throw new RuntimeException("SkillTagResponse JSON 역직렬화 오류: " + json, ex);
            }
        }
    }

    private static List<PositionTagResponse> convertJsonToPositionList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<PositionTagResponse>>() {});
        } catch (IOException e) {
            try {
                List<String> positionNames = objectMapper.readValue(json, new TypeReference<List<String>>() {});
                return positionNames.stream()
                        .map(name -> new PositionTagResponse(null, name)) // ID 없이 이름만 변환
                        .collect(Collectors.toList());
            } catch (IOException ex) {
                throw new RuntimeException("PositionTagResponse JSON 역직렬화 오류: " + json, ex);
            }
        }
    }
}
