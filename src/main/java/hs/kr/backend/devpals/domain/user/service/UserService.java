package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.dto.UserUpdateRequest;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import hs.kr.backend.devpals.infra.Aws.AwsS3Client;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PositionTagRepository positionTagRepository;
    private final SkillTagRepository skillTagRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final AwsS3Client awsS3Client;

    //개인 정보 가져오기
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.fromEntity(user);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "사용자의 정보입니다.", userResponse);

        return ResponseEntity.ok(response);
    }

    //상대방 정보 가져오기
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfoById(String token, Long id) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }

        Long requesterId = jwtTokenValidator.getUserId(token);

        if (requesterId.equals(id)) {
            throw new CustomException(ErrorException.UNAUTHORIZED); // 자신의 정보만 조회 가능하도록 제한
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.fromEntity(user);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "사용자 정보를 조회했습니다.", userResponse);

        return ResponseEntity.ok(response);
    }

    //유저 정보 업데이트
    public ResponseEntity<ApiResponse<UserResponse>> userUpdateInfo(String token, UserUpdateRequest request) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // PositionTag 처리
        PositionTagEntity positionTag = null;
        if (request.getPositionTagId() != null) {
            positionTag = positionTagRepository.findById(request.getPositionTagId())
                    .orElseThrow(() -> new CustomException(ErrorException.POSITION_NOT_FOUND));
        }

        // SkillTag 처리
        List<SkillTagEntity> skills = new ArrayList<>();
        if (request.getSkillTagIds() != null && !request.getSkillTagIds().isEmpty()) {
            skills = skillTagRepository.findAllById(request.getSkillTagIds());
        }

        // 업데이트 실행
        user.updateUserInfo(
                request.getNickname(),
                request.getBio(),
                request.getGithub(),
                positionTag,
                skills,
                request.getCareer()
        );

        userRepository.save(user); // 변경 감지 적용

        UserResponse userResponse = UserResponse.fromEntity(user);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "정보가 변경되었습니다.", userResponse);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateProfileImage(String token, MultipartFile file) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

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

        ApiResponse<String> response = new ApiResponse<>(true, "프로필 이미지가 변경되었습니다.", fileUrl);
        return ResponseEntity.ok(response);
    }

    // 파일 타입 검증
    private boolean isValidImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") ||
                lowerCaseFileName.endsWith(".jpeg") ||
                lowerCaseFileName.endsWith(".png");
    }

}
