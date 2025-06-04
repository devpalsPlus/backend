package hs.kr.backend.devpals.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.tag.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.tag.dto.SkillTagResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 목록/상세 응답 DTO")
public class ProjectAllDto {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 제목", example = "AI 추천 시스템 개발")
    private String title;

    @Schema(description = "프로젝트 소개", example = "사용자 데이터를 분석하여 맞춤형 추천을 제공하는 시스템")
    private String description;

    @Schema(description = "모집 인원", example = "5")
    private int totalMember;

    @Schema(description = "시작 예정일", example = "2025-05-01")
    private LocalDate startDate;

    @Schema(description = "예상 진행 기간", example = "3개월")
    private String estimatedPeriod;

    @Schema(description = "초보자 지원 가능 여부", example = "true")
    private Boolean isBeginner;

    @Schema(description = "모집 마감 여부", example = "false")
    private Boolean isDone;

    @Schema(description = "모집 시작일", example = "2025-04-01")
    private LocalDate recruitmentStartDate;

    @Schema(description = "모집 마감일", example = "2025-04-20")
    private LocalDate recruitmentEndDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "포지션 태그 ID 목록 (요청용)", example = "[1, 2]", accessMode = Schema.AccessMode.WRITE_ONLY)
    private List<Long> positionTagIds;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "스킬 태그 ID 목록 (요청용)", example = "[3, 4]", accessMode = Schema.AccessMode.WRITE_ONLY)
    private List<Long> skillTagIds;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "방식(method) ID (요청용)", example = "2", accessMode = Schema.AccessMode.WRITE_ONLY)
    private Long methodTypeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "조회수", example = "150", accessMode = Schema.AccessMode.READ_ONLY)
    private int views;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "작성자 정보", accessMode = Schema.AccessMode.READ_ONLY)
    private ProjectUserResponse user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "진행 방식 정보 (온라인/오프라인)", accessMode = Schema.AccessMode.READ_ONLY)
    private MethodTypeResponse methodType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "포지션 태그 정보", accessMode = Schema.AccessMode.READ_ONLY)
    private List<PositionTagResponse> positions;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "스킬 태그 정보", accessMode = Schema.AccessMode.READ_ONLY)
    private List<SkillTagResponse> skills;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "지원자 ID 목록", example = "[2, 3]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> applicantIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "참여자(수락된 지원자) ID 목록", example = "[2]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> acceptedIds;

    public static ProjectAllDto fromEntity(ProjectEntity project, List<PositionTagResponse> positions,
                                           List<SkillTagResponse> skills, MethodTypeResponse methodType, ProjectUserResponse user,
                                           List<Long> applicantIds, List<Long> acceptedIds) {
        return ProjectAllDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .totalMember(project.getTotalMember())
                .startDate(project.getStartDate())
                .estimatedPeriod(project.getEstimatedPeriod())
                .views(project.getViews())
                .isBeginner(project.isBeginner())
                .isDone(project.isDone())
                .recruitmentStartDate(project.getRecruitmentStartDate())
                .recruitmentEndDate(project.getRecruitmentEndDate())
                .skillTagIds(project.getSkillTagIds() != null ? project.getSkillTagIds() : List.of())
                .positionTagIds(project.getPositionTagIds() != null ? project.getPositionTagIds() : List.of())
                .methodTypeId(project.getMethodTypeId() != null ? project.getMethodTypeId() : 0L)
                .user(user)
                .methodType(methodType)
                .positions(positions)
                .skills(skills)
                .applicantIds(applicantIds)
                .acceptedIds(acceptedIds)
                .build();
    }
}

