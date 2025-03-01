package hs.kr.backend.devpals.domain.user.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.global.common.enums.UserLevel;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "User")
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
    @Column
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "positionTagId", referencedColumnName = "id")
    private PositionTagEntity positionTag;

    @ManyToMany
    @JoinTable(
            name = "UserSkillTag",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "skillTagId")
    )
    private Set<SkillTagEntity> skills = new HashSet<>();

    /*
    @OneToMany(mappedBy = "user")
    private List<Applicant> applicants;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    private List<Project> projects;
    */

    //스킬 추가
    public void addSkill(SkillTagEntity skill) {
        this.skills.add(skill);
    }

    //스킬 삭제
    public void removeSkill(SkillTagEntity skill) {
        this.skills.remove(skill);
    }

    // Career 객체 생성
    public List<Map<String, Object>> getCareerAsList() {
        if (this.career == null) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(this.career, List.class); // JSON -> List 변환
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorException.FAIL_JSONPROCESSING);
        }
    }

    // 리프레시 토큰 업데이트
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //비밀번호 재설정
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    //회원가입 유저 정보 저장
    public UserEntity(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
