package hs.kr.backend.devpals.domain.tag.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkillTagRequest {

    @Schema(description = "스킬 태그 이름", example = "Java")
    private String name;

    @Schema(description = "스킬 태그 이미지", type = "string", format = "binary")
    private MultipartFile img;
}
