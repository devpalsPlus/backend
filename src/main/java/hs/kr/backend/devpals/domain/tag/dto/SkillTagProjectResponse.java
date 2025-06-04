package hs.kr.backend.devpals.domain.tag.dto;

import hs.kr.backend.devpals.domain.tag.entity.SkillTagEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SkillTagProjectResponse {

    private Long id;
    private String name;
    private String img;
    private LocalDateTime createAt;

    public static SkillTagProjectResponse fromEntity(SkillTagEntity skillTagEntity){
        return SkillTagProjectResponse.builder()
                .id(skillTagEntity.getId())
                .name(skillTagEntity.getName())
                .img(skillTagEntity.getImg())
                .createAt(skillTagEntity.getCreatedAt())
                .build();
    }
}
