package hs.kr.backend.devpals.domain.user.facade;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final PositionTagRepository positionTagRepository;

    public ResponseEntity<ApiResponse<List<PositionTagEntity>>> getPositionTag() {
        List<PositionTagEntity> positionTags = positionTagRepository.findAll(); // DB에서 조회
        return ResponseEntity.ok(new ApiResponse<>(true, "포지션 태그 목록 가져오기 성공", positionTags));
    }
}
