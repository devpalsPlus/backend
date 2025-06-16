package hs.kr.backend.devpals.domain.tag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "SkillTag")
public class SkillTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String img;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SkillTagEntity(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public void update(String name, String imgUrl) {
        this.name = name;
        if (imgUrl != null) {
            this.img = imgUrl;
        }
    }
}
