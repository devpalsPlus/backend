package hs.kr.backend.devpals.domain.banner.service;

import hs.kr.backend.devpals.domain.banner.dto.BannerRequest;
import hs.kr.backend.devpals.domain.banner.dto.BannerResponse;
import hs.kr.backend.devpals.domain.banner.entity.BannerEntity;
import hs.kr.backend.devpals.domain.banner.repository.BannerRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.infra.aws.AwsS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final AwsS3Client awsS3Client;

    @Transactional
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(BannerRequest request, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(ErrorException.FILE_EMPTY);
        }

        BannerEntity banner = bannerRepository.save(request.toEntity());

        String fileName = "devpals_banner_" + banner.getId();
        String imageUrl = awsS3Client.upload(image, fileName);

        banner.update(request, imageUrl);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "배너 생성 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(Long id, BannerRequest request, MultipartFile image) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorException.BANNER_NOT_FOUND));

        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            String fileName = "devpals_banner_" + banner.getId();
            awsS3Client.delete(fileName);
            imageUrl = awsS3Client.upload(image, fileName);
        }
        banner.update(request, imageUrl);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "배너 수정 성공", null));
    }

    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteBanner(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorException.BANNER_NOT_FOUND));

        String fileName = "devpals_banner_" + banner.getId();
        awsS3Client.delete(fileName);
        bannerRepository.delete(banner);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "배너 삭제 성공", null));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getAllBanners() {
        List<BannerResponse> list = bannerRepository.findAll().stream()
                .map(BannerResponse::from)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "전체 배너 조회 성공", list));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getVisibleBanners() {
        List<BannerResponse> list = bannerRepository.findAllByIsVisibleTrue().stream()
                .map(BannerResponse::from)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "노출 중인 배너 조회 성공", list));
    }
}