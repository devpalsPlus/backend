package hs.kr.backend.devpals.domain.tag.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillTagRequest {
    private String name;
    private MultipartFile img;
}
