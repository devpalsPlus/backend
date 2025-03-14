package hs.kr.backend.devpals.domain.user.facade;

import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.global.common.ApiCustomResponse;
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
    private final Map<String, SkillTagEntity> skillTagByNameCache = new ConcurrentHashMap<>();

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
        skillTagByNameCache.clear();
        for (SkillTagEntity tag : skillTags) {
            skillTagCache.put(tag.getId(), tag);
            skillTagByNameCache.put(tag.getName(), tag);
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

    public PositionTagEntity getPositionTagById(Long id) {
        return positionTagCache.get(id);
    }

    public List<SkillTagEntity> getSkillTagsByIds(List<Long> ids) {
        return ids.stream().map(skillTagCache::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<SkillTagEntity> getSkillTagsByNames(List<String> names) {
        return names.stream().map(skillTagByNameCache::get).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
