package hs.kr.backend.devpals.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "session") // ✅ 기존 테이블명 유지
@Getter
@Setter
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userId", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "accessToken", columnDefinition = "TEXT", nullable = false)
    private String accessToken;

    @Column(name = "refreshToken", columnDefinition = "TEXT", nullable = false)
    private String refreshToken;

    @Column(name = "expiresAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expiresAt;
}
