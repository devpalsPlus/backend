package hs.kr.backend.devpals.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "PositionTag")
public class PositionTagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PositionTagEntity(String name) {
        this.name = name;
    }
}