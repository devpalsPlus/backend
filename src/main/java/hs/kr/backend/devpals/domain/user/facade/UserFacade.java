package hs.kr.backend.devpals.domain.user.facade;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final PositionTagRepository positionTagRepository;
    private final SkillTagRepository skillTagRepository;

    private final Map<Long, PositionTagEntity> positionTagCache = new ConcurrentHashMap<>();
    private final Map<Long, SkillTagEntity> skillTagCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        refreshPositionTags();
        refreshSkillTags();
    }

    public void refreshPositionTags() {
        List<PositionTagEntity> positionTags = positionTagRepository.findAll();
        positionTagCache.clear();
        positionTagCache.putAll(positionTags.stream().collect(Collectors.toMap(PositionTagEntity::getId, tag -> tag)));
    }

    public void refreshSkillTags() {
        List<SkillTagEntity> skillTags = skillTagRepository.findAll();
        skillTagCache.clear();
        for (SkillTagEntity tag : skillTags) {
            skillTagCache.put(tag.getId(), tag);
        }
    }

    public ResponseEntity<ApiCustomResponse<List<PositionTagEntity>>> getPositionTag() {
        List<PositionTagEntity> positionTags = List.copyOf(positionTagCache.values());
        ApiCustomResponse<List<PositionTagEntity>> response = new ApiCustomResponse<>(true, "포지션 태그 목록 가져오기 성공", positionTags);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiCustomResponse<List<SkillTagEntity>>> getSkillTags() {
        List<SkillTagEntity> skillTags = List.copyOf(skillTagCache.values());
        ApiCustomResponse<List<SkillTagEntity>> response = new ApiCustomResponse<>(true, "스킬 태그 목록 가져오기 성공", skillTags);
        return ResponseEntity.ok(response);
    }

    public List<PositionTagEntity> getPositionTagByIds(List<Long> ids) {
        List<PositionTagEntity> foundPositions = ids.stream()
                .map(positionTagCache::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (foundPositions.size() != ids.size()) {
            throw new CustomException(ErrorException.POSITION_NOT_FOUND);
        }

        return foundPositions;
    }

    public List<SkillTagEntity> getSkillTagsByIds(List<Long> ids) {
        List<SkillTagEntity> foundSkills = ids.stream()
                .map(skillTagCache::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (foundSkills.size() != ids.size()) {
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }

        return foundSkills;
    }

    public PositionTagEntity getPositionTagById(Long id) {
        return positionTagCache.get(id);
    }

}
