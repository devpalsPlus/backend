package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMineResponse;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProjectService {
    private final Map<Long, List<ProjectMineResponse>> projectMyCache = new HashMap<>();
    private final Map<Long, List<ProjectApplyResponse>> projectMyApplyCache = new HashMap<>();
    private final JwtTokenValidator jwtTokenValidator;
    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;
    private final UserFacade userFacade;

    @Transactional
    public ResponseEntity<ApiCustomResponse<List<ProjectMineResponse>>> getMyProject(String token) {

        Long userId = jwtTokenValidator.getUserId(token);

        if (projectMyCache.containsKey(userId)) {
            List<ProjectMineResponse> cachedProjects = new ArrayList<>(projectMyCache.get(userId));
            return ResponseEntity.ok(new ApiCustomResponse<>(true, "내가 참여한 프로젝트 조회 성공", cachedProjects));
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ApplicantEntity> applications = applicantRepository.findByUser(user)
                .stream()
                .filter(application -> application.getStatus() == ApplicantStatus.ACCEPTED)
                .collect(Collectors.toList());

        if (applications.isEmpty()) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        List<ProjectMineResponse> myProjects = applications.stream()
                .map(application -> {
                    ProjectEntity project = application.getProject();

                    List<Long> skillTagIds = project.getSkillTagsAsList();
                    List<SkillTagResponse> skillResponses = userFacade.getSkillTagResponses(skillTagIds);

                    return ProjectMineResponse.fromEntity(project, skillResponses);
                })
                .collect(Collectors.toList());

        projectMyCache.put(userId, myProjects);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "내가 참여한 프로젝트 조회 성공", myProjects));
    }

    public ResponseEntity<ApiCustomResponse<List<ProjectMineResponse>>> getUserProject(String token, Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ApplicantEntity> applications = applicantRepository.findByUser(user)
                .stream()
                .filter(application -> application.getStatus() == ApplicantStatus.ACCEPTED)
                .collect(Collectors.toList());

        if (applications.isEmpty()) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        List<ProjectMineResponse> userProjects = applications.stream()
                .map(application -> {
                    ProjectEntity project = application.getProject();
                    List<Long> skillTagIds = project.getSkillTagsAsList();
                    List<SkillTagResponse> skillResponses = userFacade.getSkillTagResponses(skillTagIds);
                    return ProjectMineResponse.fromEntity(project, skillResponses);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "사용자가 참여한 프로젝트 조회 성공", userProjects));
    }

    @Transactional
    public ResponseEntity<ApiCustomResponse<List<ProjectApplyResponse>>> getMyProjectApply(String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        if (projectMyApplyCache.containsKey(userId)) {
            return ResponseEntity.ok(new ApiCustomResponse<>(
                    true, "내 지원 프로젝트 조회 성공", new ArrayList<>(projectMyApplyCache.get(userId))));
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<ApplicantEntity> applications = applicantRepository.findByUser(user);

        if (applications.isEmpty()) {
            throw new CustomException(ErrorException.PROJECT_NOT_FOUND);
        }

        List<ProjectApplyResponse> myProjects = applications.stream()
                .map(application -> ProjectApplyResponse.fromEntity(
                        application.getProject().getTitle(),
                        application.getStatus()
                ))
                .collect(Collectors.toList());

        projectMyApplyCache.put(userId, myProjects);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "내 지원 프로젝트 조회 성공", myProjects));
    }

}
