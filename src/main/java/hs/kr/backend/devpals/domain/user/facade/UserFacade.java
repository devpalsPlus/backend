package hs.kr.backend.devpals.domain.user.facade;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final PositionTagRepository positionTagRepository;
    private final SkillTagRepository skillTagRepository;

    public ResponseEntity<ApiResponse<List<PositionTagEntity>>> getPositionTag() {
        List<PositionTagEntity> positionTags = positionTagRepository.findAll(); // DB 조회
        ApiResponse<List<PositionTagEntity>> response = new ApiResponse<>(true, "포지션 태그 목록 가져오기 성공", positionTags);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<List<SkillTagEntity>>> getSkillTags() {
        List<SkillTagEntity> skillTags = skillTagRepository.findAll(); // DB 조회
        ApiResponse<List<SkillTagEntity>> response = new ApiResponse<>(true, "스킬 태그 목록 가져오기 성공", skillTags);
        return ResponseEntity.ok(response);
    }
}
