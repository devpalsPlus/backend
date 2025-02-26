package hs.kr.backend.devpals.domain.user.entity;

import hs.kr.backend.devpals.global.common.enums.UserLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 255)
    private String nickname;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserLevel userLevel = UserLevel.Beginner;

    @Column(length = 255)
    private String github;

    @Column(columnDefinition = "JSON")
    private String career; // JSON 형태의 데이터 저장

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String refreshToken;


    /*
    @ManyToOne
    @JoinColumn(name = "positionTagId", referencedColumnName = "id")
    private PositionTag positionTag;

    @OneToMany(mappedBy = "user")
    private List<Applicant> applicants;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    private List<Project> projects;

    @OneToOne(mappedBy = "user")
    private Session session;

    @OneToMany(mappedBy = "user")
    private List<UserSkillTag> userSkillTags;
     */

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserEntity(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
