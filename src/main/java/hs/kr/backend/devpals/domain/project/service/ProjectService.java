package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.auth.service.EmailService;
import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.common.enums.MethodType;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserFacade userFacade;
    private final JwtTokenValidator jwtTokenValidator;
    private final ApplicantRepository applicantRepository;
    private final EmailService emailService;
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;
    @Qualifier("emailExecutor")
    private final Executor emailExecutor;

    private final Map<Long, ProjectAllDto> projectAllCache = new HashMap<>();

    // 프로젝트 목록 조회
    public ResponseEntity<ApiCustomResponse<List<ProjectAllDto>>> getProjectAll(List<Long> skillTagId, Long positionTagId,
                                                                                MethodType methodType, Boolean isBeginner,
                                                                                String keyword, int page, int size) {

        projectAllCache.clear();

        List<ProjectEntity> projects = projectRepository.findAll();

        projects.forEach(project -> {
            if (!projectAllCache.containsKey(project.getId())) {
                List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTagIds());
                List<PositionTagResponse> positionResponses = getPositionTagResponses(project.getPositionTagIds());

                ProjectAllDto projectDto = ProjectAllDto.fromEntity(project, positionResponses, skillResponses);
                projectAllCache.put(project.getId(), projectDto);
            }
        });

        // 필터링 적용
        List<ProjectAllDto> filteredProjects = projectAllCache.values().stream()
                .filter(project -> (methodType == null || project.getMethodType() == methodType))
                .filter(project -> (isBeginner == null || project.getIsBeginner() == isBeginner))
                .filter(project -> (keyword == null || keyword.isEmpty() ||
                        project.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        project.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(project -> (positionTagId == null || positionTagId == 0 ||
                        project.getPositionTagIds().contains(positionTagId)))
                .filter(project -> (skillTagId == null || skillTagId.isEmpty() ||
                        project.getSkillTagIds().stream().anyMatch(skillTagId::contains)))
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 목록 조회 성공", filteredProjects));
    }

    // 프로젝트 개수 조회
    public ResponseEntity<ApiCustomResponse<ProjectCountResponse>> getProjectCount() {
        long totalProjectCount = projectRepository.count();
        long ongoingProjectCount = projectRepository.countByIsDoneFalse();
        long endProjectCount = totalProjectCount - ongoingProjectCount;

        ProjectCountResponse responseData = new ProjectCountResponse(totalProjectCount, ongoingProjectCount, endProjectCount);
        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 개수 조회 성공", responseData));
    }

    // 프로젝트 업데이트
    @Transactional
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> updateProject(Long projectId, String token, ProjectAllDto request) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if (!project.getAuthorId().equals(userId)) {
            throw new CustomException(ErrorException.FAIL_PROJECT_UPDATE);
        }

        project.updateProject(request);
        projectRepository.save(project);

        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkillTagIds());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(request.getPositionTagIds());

        ProjectAllDto updatedProject = ProjectAllDto.fromEntity(project, positionResponses, skillResponses);
        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 업데이트 완료", updatedProject));
    }

    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> projectSignup(ProjectAllDto request, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = ProjectEntity.fromRequest(request, userId);
        ProjectEntity savedProject = projectRepository.save(project);

        List<SkillTagResponse> skillResponses = getSkillTagResponses(savedProject.getSkillTagIds());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(savedProject.getPositionTagIds());

        ProjectAllDto responseDto = ProjectAllDto.fromEntity(savedProject, positionResponses, skillResponses);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 등록 완료", responseDto));
    }


    // 특정 프로젝트 조회
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> getProjectList(Long projectId) {
        ProjectAllDto project = projectAllCache.get(projectId);
        if (project == null) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }
        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 조회 성공", project));
    }

    // 스킬 태그 변환 (ID 리스트 -> DTO 리스트)
    private List<SkillTagResponse> getSkillTagResponses(List<Long> skillTagIds) {
        if (skillTagIds == null || skillTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userFacade.getSkillTagsByIds(skillTagIds).stream()
                .map(skill -> new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg()))
                .collect(Collectors.toList());
    }


    // 포지션 태그 변환 (ID 리스트 -> DTO 리스트)
    private List<PositionTagResponse> getPositionTagResponses(List<Long> positionTagIds) {
        if (positionTagIds == null || positionTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userFacade.getPositionTagByIds(positionTagIds).stream()
                .map(position -> new PositionTagResponse(position.getId(), position.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<ApiCustomResponse<ProjectCloseResponse>> closeProject(Long projectId, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        // 프로젝트 조회 및 유효성 검사
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

//        if (!project.getAuthorId().equals(userId)) {
//            throw new CustomException(ErrorException.FAIL_PROJECT_UPDATE);
//        }

        // 'WAITING' 상태의 지원자들을 'REJECTED'로 변경
        List<ApplicantEntity> applicants = applicantRepository.findByProject(project);
        applicants.stream()
                .filter(applicant -> applicant.getStatus().equals(ApplicantStatus.WAITING))
                .forEach(applicant -> applicant.updateStatus(ApplicantStatus.REJECTED));

        project.updateIsDone(true);

        // 변경된 상태 저장
        projectRepository.save(project);
        applicantRepository.saveAll(applicants);

        CompletableFuture.runAsync(() -> emailService.sendEmailsAsync(applicants, project), emailExecutor);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 모집 종료 성공", ProjectCloseResponse.fromEntity(project)));
    }

    @Transactional
    public CompletableFuture<Void> closeProject(List<ProjectEntity> projects) {
        // 트랜잭션 내에서 미리 데이터를 로드하여 LazyInitializationException 방지
        Map<ProjectEntity, List<ApplicantEntity>> projectApplicantsMap = projects.stream()
                .collect(Collectors.toMap(project -> project, applicantRepository::findByProject));

        List<CompletableFuture<Void>> futures = projects.stream()
                .map(project -> CompletableFuture.runAsync(() -> {
                    List<ApplicantEntity> applicants = projectApplicantsMap.get(project);

                    applicants.stream()
                            .filter(applicant -> applicant.getStatus().equals(ApplicantStatus.WAITING))
                            .forEach(applicant -> applicant.updateStatus(ApplicantStatus.REJECTED));

                    project.updateIsDone(true);
                }, taskExecutor)) // 비즈니스 로직 실행 (taskExecutor 사용)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> projectRepository.saveAll(projects)) // ✅ 프로젝트 상태 저장
                .thenCompose(ignored -> CompletableFuture.runAsync(() -> { // ✅ 이메일 전송 (순서 보장)
                    projects.forEach(project -> {
                        List<ApplicantEntity> applicants = projectApplicantsMap.get(project);
                        emailService.sendEmailsAsync(applicants, project);
                    });
                }, emailExecutor));
    }
}
