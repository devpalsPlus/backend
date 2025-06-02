package hs.kr.backend.devpals.domain.Inquiry.service;

import hs.kr.backend.devpals.domain.Inquiry.dto.InquiryDto;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryImageEntity;
import hs.kr.backend.devpals.domain.Inquiry.repository.InquiryRepository;
import hs.kr.backend.devpals.domain.faq.service.FaqAdminService;
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
public class InquiryAdminService {
    private final InquiryRepository inquiryRepository;
    private final AwsS3Client awsS3Client;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final FaqAdminService faqAdminService;


    @Transactional
    public ResponseEntity<ApiResponse<String>> registerAnswer(String token, Long inquiryId, String answer) {
        faqAdminService.validateAdmin(token);

        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));

        if (inquiry.getAnswer() != null) {
            throw new CustomException(ErrorException.ALREADY_ANSWERED);
        }

        inquiry.writeAnswer(answer);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "답변 등록 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateAnswer(String token, Long inquiryId, String answer) {
        faqAdminService.validateAdmin(token);

        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorException.INQUIRY_NOT_FOUND));

        if (inquiry.getAnswer() == null) {
            throw new CustomException(ErrorException.ANSWER_NOT_FOUND);
        }

        inquiry.updateAnswer(answer);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "답변 수정 성공", null));
    }
}
