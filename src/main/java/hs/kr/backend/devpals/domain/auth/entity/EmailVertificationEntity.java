package hs.kr.backend.devpals.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "AuthentiCode")
public class EmailVertificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isUsed = false;

    public EmailVertificationEntity(String userEmail, String code, LocalDateTime expiresAt) {
        this.userEmail = userEmail;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    // 인증 코드 사용 처리
    public void useCode() {
        this.isUsed = true;
    }
}
