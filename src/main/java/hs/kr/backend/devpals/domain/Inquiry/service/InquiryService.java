package hs.kr.backend.devpals.domain.Inquiry.service;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryResponse;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryImageEntity;
import hs.kr.backend.devpals.domain.Inquiry.repository.InquiryRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import hs.kr.backend.devpals.infra.aws.AwsS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final AwsS3Client awsS3Client;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<ApiResponse<String>> createInquiry(String token, InquiryResponse request, List<MultipartFile> images) {
        Long userId = jwtTokenValidator.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        int inquiryCount = inquiryRepository.countByUserId(userId);

        InquiryEntity inquiry = InquiryEntity.from(request, user);
        inquiryRepository.save(inquiry);

        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);

                if (file == null || file.isEmpty()) {
                    continue;
                }

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || !awsS3Client.isValidImageFile(originalFilename)) {
                    throw new CustomException(ErrorException.INVALID_FILE_TYPE);
                }

                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String fileName = "devpals_inquiry" + userId + "-" + (inquiryCount + 1) + "-" + (i + 1) + fileExtension;

                String imageUrl = awsS3Client.upload(file, fileName);

                InquiryImageEntity imageEntity = InquiryImageEntity.from(inquiry, imageUrl);
                inquiry.getImages().add(imageEntity);
            }
        }
        return ResponseEntity.ok(new ApiResponse<>(200, true, "문의 작성 성공", null));
    }


    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteInquiry(String token, Long inquiryId) {
        Long userId = jwtTokenValidator.getUserId(token);

        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));

        if (!inquiry.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorException.NOT_INQUIRY_DELETE);
        }

        if (inquiry.getImages() != null) {
            for (InquiryImageEntity image : inquiry.getImages()) {
                awsS3Client.delete(image.getImageUrl());
            }
        }

        inquiryRepository.delete(inquiry);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "문의 삭제 성공", null));
    }

}
