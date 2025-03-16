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
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
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
import org.springframework.web.bind.annotation.RequestParam;

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
    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;
    private final EmailService emailService;
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;
    @Qualifier("emailExecutor")
    private final Executor emailExecutor;

    private final Map<Long, ProjectAllDto> projectAllCache = new HashMap<>();

    public ResponseEntity<ApiCustomResponse<List<ProjectAllDto>>> getProjectAll(List<Long> skillTagId, Long positionTagId,
                                                                                MethodType methodType, Boolean isBeginner,
                                                                                String keyword, int page, int size){

        projectAllCache.clear();

        List<ProjectEntity> projects = projectRepository.findAll();

        projects.forEach(project -> {
            if (!projectAllCache.containsKey(project.getId())) {
                UserEntity user = userRepository.findById(project.getAuthorId())
                        .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

                List<SkillTagResponse> skillResponses = getSkillTagResponses(project.getSkillTagsAsList());
                List<PositionTagResponse> positionResponses = getPositionTagResponses(project.getPositionTagsAsList());

                ProjectAllDto projectDto = ProjectAllDto.fromEntity(project, user.getNickname(), user.getProfileImg(), skillResponses, positionResponses);

                projectAllCache.put(project.getId(), projectDto);
            }
        });

        //
        List<ProjectAllDto> filteredProjects = projectAllCache.values().stream()
                .filter(project -> (methodType == null || project.getMethodType() == methodType))
                .filter(project -> (isBeginner == null || project.getIsBeginner() == isBeginner))
                .filter(project -> (keyword == null || keyword.isEmpty() ||
                        project.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        project.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(project -> (positionTagId == null || positionTagId == 0 ||
                        project.getPositions().stream().anyMatch(p -> p.getId().equals(positionTagId))))
                .filter(project -> (skillTagId == null || skillTagId.isEmpty() ||
                        project.getSkills().stream().anyMatch(s -> skillTagId.contains(s.getId()))))
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 목록 조회 성공", filteredProjects));
    }


    // 프로젝트 개수
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

        getSkillTagResponses(request.getSkills());
        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(project.getPositionTagsAsList());

        project.updateProject(request, request.getPositions(), skillResponses);
        projectRepository.save(project);

        ProjectAllDto updatedProject = ProjectAllDto.fromEntity(project, request.getAuthorNickname(),
                                                                request.getAuthorImage(), skillResponses, positionResponses);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 업데이트 완료", updatedProject));
    }


    // 프로젝트 등록
    @Transactional
    public ResponseEntity<ApiCustomResponse<Long>> projectSignup(ProjectAllDto request, String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<SkillTagResponse> skillResponses = getSkillTagResponses(request.getSkills());
        List<PositionTagResponse> positionResponses = getPositionTagResponses(request.getPositions());

        ProjectEntity project = ProjectEntity.fromRequest(request, positionResponses, userId, skillResponses);
        ProjectEntity savedProject = projectRepository.save(project);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 등록 완료", savedProject.getId()));
    }


    // 프로젝트 목록 조회
    public ResponseEntity<ApiCustomResponse<ProjectAllDto>> getProjectList(Long projectId) {
        ProjectAllDto project = projectAllCache.get(projectId);

        if (project == null) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로젝트 조회 성공", project));
    }


    public List<SkillTagResponse> getSkillTagResponses(List<SkillTagResponse> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }

        List<Long> skillIds = skills.stream()
                .map(SkillTagResponse::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<SkillTagEntity> skillEntities = userFacade.getSkillTagsByIds(skillIds);

        return skillEntities.stream()
                .map(skill -> new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg()))
                .collect(Collectors.toList());
    }

    public List<PositionTagResponse> getPositionTagResponses(List<PositionTagResponse> positions) {
        if (positions == null || positions.isEmpty()) {
            throw new CustomException(ErrorException.POSITION_NOT_FOUND);
        }

        List<Long> positionIds = positions.stream()
                .map(PositionTagResponse::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<PositionTagEntity> positionEntities = userFacade.getPositionTagByIds(positionIds);

        return positionEntities.stream()
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
