package hs.kr.backend.devpals.domain.user.dto;

import hs.kr.backend.devpals.domain.tag.entity.SkillTagEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserSkillTagResponse {

    private Long id;
    private String name;
    private String img;
    private LocalDateTime createdAt;


    public static UserSkillTagResponse fromEntity(SkillTagEntity skillTag) {
        return UserSkillTagResponse.builder()
                .id(skillTag.getId())
                .name(skillTag.getName())
                .img(skillTag.getImg())
                .createdAt(skillTag.getCreatedAt())
                .build();
    }
}
