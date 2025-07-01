package hs.kr.backend.devpals.domain.banner.controller;

import hs.kr.backend.devpals.domain.banner.dto.BannerRequest;
import hs.kr.backend.devpals.domain.banner.dto.BannerResponse;
import hs.kr.backend.devpals.domain.banner.service.BannerService;
import hs.kr.backend.devpals.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
@Tag(name = "Banner API", description = "배너 관련 API")
public class BannerController {

    private final BannerService bannerService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(
            summary = "배너 생성",
            description = "이미지, 노출 여부, 노출 방식(상시/기간) 및 기간(선택)을 포함하여 배너를 생성합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배너 생성 성공")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
            @RequestPart BannerRequest request,
            @RequestPart MultipartFile image
    ) {
        return bannerService.createBanner(request, image);
    }

    @PatchMapping(value = "/{bannerId}", consumes = "multipart/form-data")
    @Operation(
            summary = "배너 수정",
            description = "배너 ID를 기반으로 기존 배너 정보를 이미지 포함 수정합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배너 수정 성공")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @Parameter(description = "수정할 배너 ID") @PathVariable Long bannerId,
            @RequestPart BannerRequest request,
            @RequestPart(required = false) MultipartFile image
    ) {
        return bannerService.updateBanner(bannerId, request, image);
    }

    @DeleteMapping("/{bannerId}")
    @Operation(
            summary = "배너 삭제",
            description = "배너 ID를 기반으로 배너를 삭제합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배너 삭제 성공")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(
            @Parameter(description = "삭제할 배너 ID") @PathVariable Long bannerId
    ) {
        return bannerService.deleteBanner(bannerId);
    }

    @GetMapping
    @Operation(
            summary = "전체 배너 조회",
            description = "관리자 페이지 또는 시스템용 전체 배너 목록을 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전체 배너 조회 성공")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getAllBanners() {
        return bannerService.getAllBanners();
    }

    @GetMapping("/visible")
    @Operation(
            summary = "노출 중 배너 조회",
            description = "isVisible=true인 노출 중인 배너만 필터링하여 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "노출 중 배너 조회 성공")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getVisibleBanners() {
        return bannerService.getVisibleBanners();
    }
}
