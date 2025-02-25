package hs.kr.backend.devpals.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "session") // ✅ 기존 테이블명 유지
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime expiresAt;

    public SessionEntity updateTokens(String newAccessToken, String newRefreshToken, LocalDateTime newExpiresAt) {
        return SessionEntity.builder()
                .id(this.id)
                .userId(this.userId)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresAt(newExpiresAt)
                .build();
    }
}
