package hs.kr.backend.devpals.domain.user.facade;

import hs.kr.backend.devpals.domain.user.dto.PositionTagRequest;
import hs.kr.backend.devpals.domain.user.dto.PositionTagResponse;
import hs.kr.backend.devpals.domain.user.dto.SkillTagRequest;
import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.domain.user.entity.PositionTagEntity;
import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import hs.kr.backend.devpals.domain.user.repository.PositionTagRepository;
import hs.kr.backend.devpals.domain.user.repository.SkillTagRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.infra.aws.AwsS3Client;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    private final AwsS3Client awsS3Client;
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

    public ResponseEntity<ApiResponse<SkillTagEntity>> createSkillTag(SkillTagRequest request) {
        String ext = getSkillExtension(request.getImg().getOriginalFilename());
        String fileName = request.getName().trim().replaceAll("\\s+", "_") + "." + ext;

        String imgUrl = awsS3Client.upload(request.getImg(), fileName);

        SkillTagEntity skillTag = new SkillTagEntity(request.getName(), imgUrl);
        SkillTagEntity saved = skillTagRepository.save(skillTag);
        refreshSkillTags();

        return ResponseEntity.ok(new ApiResponse<>(200, true, "스킬 태그 등록 성공", saved));
    }

    public ResponseEntity<ApiResponse<PositionTagEntity>> createPositionTag(PositionTagRequest request) {
        PositionTagEntity positionTag = new PositionTagEntity(request.getName());
        PositionTagEntity saved = positionTagRepository.save(positionTag);
        refreshPositionTags();
        return ResponseEntity.ok(new ApiResponse<>(200, true, "포지션 태그 등록 성공", saved));
    }

    public ResponseEntity<ApiResponse<List<SkillTagEntity>>> getSkillTags() {
        List<SkillTagEntity> skillTags = List.copyOf(skillTagCache.values());
        ApiResponse<List<SkillTagEntity>> response = new ApiResponse<>(200, true, "스킬 태그 목록 가져오기 성공", skillTags);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<List<PositionTagEntity>>> getPositionTag() {
        List<PositionTagEntity> positionTags = List.copyOf(positionTagCache.values());
        ApiResponse<List<PositionTagEntity>> response = new ApiResponse<>(200, true, "포지션 태그 목록 가져오기 성공", positionTags);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<String>> deleteSkillTag(Long skillTagId) {
        SkillTagEntity skillTag = skillTagRepository.findById(skillTagId)
                .orElseThrow(() -> new CustomException(ErrorException.SKILL_NOT_FOUND));

        String imgUrl = skillTag.getImg();
        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
        awsS3Client.delete(fileName);

        skillTagRepository.delete(skillTag);
        skillTagCache.remove(skillTagId);

        ApiResponse<String> response = new ApiResponse<>(200, true, "스킬 태그 삭제 성공", null);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<String>> deletePositionTag(Long positionTagId) {
        PositionTagEntity positionTag = positionTagRepository.findById(positionTagId)
                .orElseThrow(() -> new CustomException(ErrorException.POSITION_NOT_FOUND));

        positionTagRepository.delete(positionTag);
        positionTagCache.remove(positionTagId);

        ApiResponse<String> response = new ApiResponse<>(200, true, "포지션 태그 삭제 성공", null);
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

    private String getSkillExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new CustomException(ErrorException.SKILL_NOT_FOUND);
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    //
    public List<SkillTagResponse> getSkillTagResponses(List<Long> skillTagIds) {

        List<SkillTagEntity> skillEntities = getSkillTagsByIds(skillTagIds);
        if (skillEntities == null || skillEntities.isEmpty()) {
            return Collections.emptyList();
        }

        return skillEntities.stream()
                .map(skill -> new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg()))
                .collect(Collectors.toList());
    }

    public List<PositionTagResponse> getPositionTagResponses(List<Long> positionTagIds) {
        List<PositionTagEntity> positionEntities = getPositionTagByIds(positionTagIds);
        if (positionEntities == null || positionEntities.isEmpty()) {
            return Collections.emptyList();
        }

        return positionEntities.stream()
                .map(position -> new PositionTagResponse(position.getId(), position.getName()))
                .collect(Collectors.toList());
    }

}
