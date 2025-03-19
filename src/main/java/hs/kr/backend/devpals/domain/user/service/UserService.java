package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.project.dto.ProjectApplyResponse;
import hs.kr.backend.devpals.domain.project.dto.ProjectMineResponse;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.dto.UserUpdateRequest;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import hs.kr.backend.devpals.infra.Aws.AwsS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final AwsS3Client awsS3Client;
    private final ApplicantRepository applicantRepository;
    private final UserFacade userFacade;

    private final Map<Long, List<ProjectMineResponse>> projectMyCache = new HashMap<>();
    private final Map<Long, List<ProjectApplyResponse>> projectMyApplyCache = new HashMap<>();

    //개인 정보 가져오기
    public ResponseEntity<ApiCustomResponse<UserResponse>> getUserInfo(String token) {

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.fromEntity(user, userFacade);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "사용자의 정보입니다.", userResponse));
    }

    //상대방 정보 가져오기
    public ResponseEntity<ApiCustomResponse<UserResponse>> getUserInfoById(String token, Long id) {

        Long requesterId = jwtTokenValidator.getUserId(token);

        if (requesterId.equals(id)) {
            throw new CustomException(ErrorException.UNAUTHORIZED); // 자신의 정보만 조회 가능하도록 제한
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.fromEntity(user, userFacade);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "사용자 정보를 조회했습니다.", userResponse));
    }

    //유저 정보 업데이트
    @Transactional
    public ResponseEntity<ApiCustomResponse<UserResponse>> userUpdateInfo(String token, UserUpdateRequest request) {

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        List<Long> positionIds = request.getPositionTagIds();
        List<Long> skillIds = request.getSkillTagIds();

        if (positionIds == null) {
            throw new CustomException(ErrorException.POSITION_NOT_FOUND);
        } else if (skillIds == null){
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }

        user.updateUserInfo(
                request.getNickname(),
                request.getBio(),
                request.getGithub(),
                positionIds,
                skillIds,
                request.getCareer()
        );

        userRepository.save(user);

        UserResponse userResponse = UserResponse.fromEntity(user, userFacade);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "정보가 변경되었습니다.", userResponse));
    }

    @Transactional
    public ResponseEntity<ApiCustomResponse<String>> updateProfileImage(String token, MultipartFile file) {

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new CustomException(ErrorException.INVALID_FILE_TYPE);
        }

        // S3에 저장할 파일명 생성 (ex: user_2_profile.jpg)
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "devpals_user" + userId + "_profile" + fileExtension;

        // 기존 프로필 이미지 삭제 (필요할 경우)
        if (user.getProfileImg() != null) {
            String oldFileName = user.getProfileImg().substring(user.getProfileImg().lastIndexOf("/") + 1);
            awsS3Client.delete(oldFileName);
        }

        // S3에 파일 업로드
        String fileUrl = awsS3Client.upload(file, fileName);

        // 업로드된 URL을 DB에 저장
        user.updateProfileImage(fileUrl);
        userRepository.save(user);

        return ResponseEntity.ok(new ApiCustomResponse<>(true, "프로필 이미지가 변경되었습니다.", fileUrl));
    }

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
                    List<SkillTagResponse> skillResponses = getSkillTagResponses(skillTagIds);

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
                    List<SkillTagResponse> skillResponses = getSkillTagResponses(skillTagIds);
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

    // 파일 타입 검증
    private boolean isValidImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") ||
                lowerCaseFileName.endsWith(".jpeg") ||
                lowerCaseFileName.endsWith(".png");
    }


    private List<SkillTagResponse> getSkillTagResponses(List<Long> skillTagIds) {
        if (skillTagIds == null || skillTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<SkillTagEntity> skillEntities = userFacade.getSkillTagsByIds(skillTagIds);
        if (skillEntities == null || skillEntities.isEmpty()) {
            return Collections.emptyList(); // 조회된 값이 없으면 빈 리스트 반환
        }

        return skillEntities.stream()
                .map(skill -> new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg()))
                .collect(Collectors.toList());
    }
}
