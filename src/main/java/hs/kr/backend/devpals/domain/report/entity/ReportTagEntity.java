package hs.kr.backend.devpals.domain.report.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "ReportTag")
public class ReportTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ReportTagEntity(String name) {
        this.name = name;
    }
}
