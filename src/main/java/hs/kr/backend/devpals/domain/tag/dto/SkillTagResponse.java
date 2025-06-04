package hs.kr.backend.devpals.domain.tag.dto;

import hs.kr.backend.devpals.domain.tag.entity.SkillTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillTagResponse {

    private Long id;
    private String name;
    private String img;

    public static SkillTagResponse fromEntity(SkillTagEntity skill) {
        return new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg());
    }

}
