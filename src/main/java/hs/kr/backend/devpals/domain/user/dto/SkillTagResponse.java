package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillTagResponse {

    private String skillName;
    private String skillImg;

    // SkillTagEntity → SkillTagDTO 변환하는 정적 메서드
    public static SkillTagResponse fromEntity(SkillTagEntity skill) {
        return new SkillTagResponse(skill.getName(), skill.getImg());
    }
}
