package hs.kr.backend.devpals.domain.tag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hs.kr.backend.devpals.domain.tag.entity.SkillTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillTagResponse {

    private Long id;
    private String name;
    private String img;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static SkillTagResponse fromEntity(SkillTagEntity skill) {
        return new SkillTagResponse(skill.getId(), skill.getName(), skill.getImg(), skill.getUpdatedAt());
    }

}
