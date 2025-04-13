package hs.kr.backend.devpals.domain.user.entity;

import hs.kr.backend.devpals.domain.user.dto.SkillTagResponse;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

}
