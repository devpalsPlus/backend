package hs.kr.backend.devpals.domain.project.service;

import hs.kr.backend.devpals.domain.auth.service.AuthEmailService;
import hs.kr.backend.devpals.domain.project.dto.*;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.facade.ProjectFacade;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.domain.user.service.AlarmService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.AlarmFilter;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserFacade userFacade;
    private final ProjectFacade projectFacade;
    private final JwtTokenValidator jwtTokenValidator;
    private final ApplicantRepository applicantRepository;
    private final AuthEmailService authEmailService;
    private final AlarmService alarmService;
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;
    @Qualifier("emailExecutor")
    private final Executor emailExecutor;

    private final Map<Long, ProjectAllDto> projectAllCache = new LinkedHashMap<>();

    // 프로젝트 목록 조회
    @Transactional
    public ResponseEntity<ApiResponse<ProjectListResponse>> getProjectAll(
            List<Long> skillTagId, Long positionTagId,
            Long methodTypeId, Boolean isBeginner,
            String keyword, int page) {

        if (projectAllCache.isEmpty()) {
            List<ProjectEntity> projects = projectRepository.findAllByOrderByCreatedAtDesc();
            projects.forEach(project -> {
                if (!projectAllCache.containsKey(project.getId())) {
                    projectAllCache.put(project.getId(), convertToDto(project));
                }
            });
        }

        // 전체 캐시에서 필터링 먼저 수행
        List<ProjectAllDto> filteredProjectsAll = projectAllCache.values().stream()
                .filter(project -> !isBeginner || project.getIsBeginner())
                .filter(project -> keyword == null || keyword.isEmpty() ||
                        project.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        project.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .filter(project -> methodTypeId == null || methodTypeId <= 0L ||
                        Objects.equals(project.getMethodTypeId(), methodTypeId))
                .filter(project -> positionTagId == null || positionTagId <= 0 ||
                        project.getPositionTagIds().contains(positionTagId))
                .filter(project -> skillTagId == null || skillTagId.isEmpty() ||
                        project.getSkillTagIds().stream().anyMatch(skillTagId::contains))
                .collect(Collectors.toList());

        // 필터링된 결과 기준으로 total, lastPage 계산
        int totalProjects = filteredProjectsAll.size();
        int lastPage = (int) Math.ceil((double) totalProjects / 12);

        // 페이지네이션 적용
        List<ProjectAllDto> filteredProjects = filteredProjectsAll.stream()
                .skip((page - 1) * 12)
                .limit(12)
                .collect(Collectors.toList());

        ProjectListResponse responseDto = new ProjectListResponse(page, lastPage, totalProjects, filteredProjects);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "프로젝트 목록 조회 성공", responseDto));
    }


    // 프로젝트 개수 조회
    public ResponseEntity<ApiResponse<ProjectCountResponse>> getProjectCount() {
        long totalProjectCount = projectRepository.count();
        long ongoingProjectCount = projectRepository.countByIsDoneFalse();
        long endProjectCount = totalProjectCount - ongoingProjectCount;

        ProjectCountResponse responseData = new ProjectCountResponse(totalProjectCount, ongoingProjectCount, endProjectCount);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "프로젝트 개수 조회 성공", responseData));
    }

    // 프로젝트 업데이트
    @Transactional
    public ResponseEntity<ApiResponse<ProjectAllDto>> updateProject(Long projectId, String token, ProjectAllDto request) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        if (!project.getUserId().equals(userId)) {
            throw new CustomException(ErrorException.FAIL_PROJECT_UPDATE);
        }

        project.updateProject(request);
        projectRepository.save(project);

        ProjectAllDto updatedProject = convertToDto(project);
        projectAllCache.put(projectId, updatedProject);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "프로젝트 업데이트 완료", null));
    }

    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiResponse<ProjectAllDto>> projectSignup(ProjectAllDto request, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        ProjectEntity project = ProjectEntity.fromRequest(request, userId);
        ProjectEntity savedProject = projectRepository.save(project);

        ProjectAllDto responseDto = convertToDto(savedProject);
        projectAllCache.put(savedProject.getId(), responseDto);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "프로젝트 등록 완료", null));
    }

    // 특정 프로젝트 조회
    @Transactional
    public ResponseEntity<ApiResponse<ProjectAllDto>> getProjectDetail(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorException.PROJECT_NOT_FOUND));

        project.increaseView();

        ProjectAllDto dto = convertToDto(project);

        projectAllCache.put(projectId, dto);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "프로젝트 상세 정보입니다.", dto));
    }

    public ResponseEntity<ApiResponse<List<ProjectAuthoredResponse>>> getMyProject(String token) {
        Long userId = jwtTokenValidator.getUserId(token);
        List<ProjectEntity> projects = projectRepository.findProjectsByUserId(userId);
        List<ProjectAuthoredResponse> projectAuthoredResponses = projects.stream()
                .map(project -> ProjectAuthoredResponse.fromEntity(
                        project,
                        userFacade.getPositionTagByIds(project.getPositionTagIds()),
                        userFacade.getSkillTagsByIds(project.getSkillTagIds()),
                        projectFacade.getMethodTypeById(project.getMethodTypeId())
                ))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "내 프로젝트 조회 성공", projectAuthoredResponses));
    }

    // 프로젝트 모집 종료하기
    @Transactional
    public ResponseEntity<ApiResponse<ProjectCloseResponse>> closeProject(Long projectId, String token) {
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

        ProjectAllDto updatedProjectDto = convertToDto(project);
        projectAllCache.put(projectId, updatedProjectDto);

        CompletableFuture.runAsync(() -> authEmailService.sendEmailsAsync(applicants, project), emailExecutor);

        MethodTypeResponse methodTypeResponse = projectFacade.getMethodTypeResponse(project.getMethodTypeId());

        alarmService.sendAlarm(applicants,project);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "프로젝트 모집 종료 성공", ProjectCloseResponse.fromEntity(project, methodTypeResponse)));
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
                        authEmailService.sendEmailsAsync(applicants, project);
                        alarmService.sendAlarm(applicants, project);
                    });
                }, emailExecutor));
    }

    public void refreshProjectCacheByAuthor(Long authorId) {
        List<ProjectEntity> projects = projectRepository.findProjectsByUserId(authorId);
        for (ProjectEntity project : projects) {
            ProjectAllDto updatedDto = convertToDto(project);
            projectAllCache.put(project.getId(), updatedDto);
        }
    }


    private ProjectAllDto convertToDto(ProjectEntity project) {
        List<SkillTagResponse> skillResponses = userFacade.getSkillTagResponses(project.getSkillTagIds());
        List<PositionTagResponse> positionResponses = userFacade.getPositionTagResponses(project.getPositionTagIds());
        MethodTypeResponse methodTypeResponse = projectFacade.getMethodTypeResponse(project.getMethodTypeId());
        List<ApplicantEntity> applicants = applicantRepository.findByProject(project);

        List<Long> applicantIds = applicants.stream()
                .map(applicant -> applicant.getUser().getId())
                .toList();

        List<Long> acceptedIds = applicants.stream()
                .filter(ApplicantEntity::isAccepted)
                .map(applicant -> applicant.getUser().getId())
                .toList();

        UserEntity userEntity = userRepository.findById(project.getUserId())
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        ProjectUserResponse user = ProjectUserResponse.fromEntity(userEntity);

        return ProjectAllDto.fromEntity(project, positionResponses, skillResponses, methodTypeResponse, user, applicantIds, acceptedIds);
    }

}
