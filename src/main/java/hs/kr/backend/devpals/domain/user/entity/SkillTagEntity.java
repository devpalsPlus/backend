package hs.kr.backend.devpals.domain.user.entity;

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
    private Integer id;

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
}
