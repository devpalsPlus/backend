package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryDto;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.Inquiry.repository.InquiryRepository;
import hs.kr.backend.devpals.domain.project.dto.CommentDTO;
import hs.kr.backend.devpals.domain.project.entity.CommentEntity;
import hs.kr.backend.devpals.domain.project.repository.CommentRepoisitory;
import hs.kr.backend.devpals.domain.project.repository.RecommentRepository;
import hs.kr.backend.devpals.domain.project.service.ProjectService;
import hs.kr.backend.devpals.domain.user.dto.CommentInquiryDto;
import hs.kr.backend.devpals.domain.user.dto.UserResponse;
import hs.kr.backend.devpals.domain.user.dto.UserUpdateRequest;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.facade.UserFacade;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import hs.kr.backend.devpals.infra.Aws.AwsS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserFacade userFacade;
    private final UserRepository userRepository;
    private final CommentRepoisitory commentRepository;
    private final RecommentRepository recommentRepository;
    private final InquiryRepository inquiryRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final AwsS3Client awsS3Client;
    private final ProjectService projectService;

    //개인 정보 가져오기
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(String token) {

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.fromEntity(user, userFacade);

        return ResponseEntity.ok(new ApiResponse<>(true, "사용자의 정보입니다.", userResponse));
    }

    //상대방 정보 가져오기
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfoById(String token, Long id) {

        Long requesterId = jwtTokenValidator.getUserId(token);

        if (requesterId.equals(id)) {
            throw new CustomException(ErrorException.UNAUTHORIZED); // 자신의 정보만 조회 가능하도록 제한
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.fromEntity(user, userFacade);

        return ResponseEntity.ok(new ApiResponse<>(true, "사용자 정보를 조회했습니다.", userResponse));
    }

    public ResponseEntity<ApiResponse<String>> userNicknameCheck(String token, String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);

        if (exists) {
            return ResponseEntity.ok(new ApiResponse<>(false, "중복된 닉네임입니다.", null));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(true, "사용 가능한 닉네임입니다.", null));
        }
    }

    //유저 정보 업데이트
    @Transactional
    public ResponseEntity<ApiResponse<UserResponse>> userUpdateInfo(String token, UserUpdateRequest request) {

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
                request.getBeginner(),
                positionIds,
                skillIds,
                request.getCareer()
        );

        userRepository.save(user);

        projectService.refreshProjectCacheByAuthor(userId);

        UserResponse userResponse = UserResponse.fromEntity(user, userFacade);

        return ResponseEntity.ok(new ApiResponse<>(true, "정보가 변경되었습니다.", userResponse));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateProfileImage(String token, MultipartFile file) {

        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !awsS3Client.isValidImageFile(originalFilename)) {
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

        return ResponseEntity.ok(new ApiResponse<>(true, "프로필 이미지가 변경되었습니다.", fileUrl));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getMyComments(String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<CommentEntity> comments = commentRepository.findByUserId(userId);

        List<CommentDTO> commentDTOs = comments.stream()
                .map(comment -> {
                    int recommentCount = recommentRepository.countByCommentId(comment.getId());
                    return CommentDTO.fromEntity(comment, recommentCount);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "작성한 댓글 목록입니다.", commentDTOs));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<InquiryDto>>> getMyInquiries(String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        List<InquiryEntity> inquiries = inquiryRepository.findByUserId(userId);

        List<InquiryDto> inquiryDTOs = inquiries.stream()
                .map(InquiryDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "작성한 문의글 목록입니다.", inquiryDTOs));
    }
}
