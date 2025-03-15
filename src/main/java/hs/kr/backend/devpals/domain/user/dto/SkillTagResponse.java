package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillTagResponse {

    private Long id;
    private String skillName;
    private String skillImg;

    public static SkillTagResponse fromEntity(SkillTagEntity skill) {
        return new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg());
    }

}
