package hs.kr.backend.devpals.domain.user.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.domain.user.convert.LongListConverter;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private Boolean beginner;

    @Column(length = 255)
    private String github;

    @Column(columnDefinition = "JSON")
    private String career; // JSON 형태의 데이터 저장

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Convert(converter = LongListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Long> positionIds;

    @Convert(converter = LongListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Long> skillIds;

    /*
    @OneToMany(mappedBy = "user")
    private List<Applicant> applicants;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    private List<Project> projects;
    */

    // 유저 업데이트
    public void updateUserInfo(String nickname, String bio, String github, List<Long> positionIds,
                               List<Long> skillIds, List<Map<String, Object>> career) {
        if (nickname != null) { this.nickname = nickname; }
        if (bio != null) { this.bio = bio; }
        if (github != null) { this.github = github; }
        if (positionIds != null) { this.positionIds = new ArrayList<>(positionIds); }
        if (skillIds != null) { this.skillIds = new ArrayList<>(skillIds); }
        if (career != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                this.career = objectMapper.writeValueAsString(career);
            } catch (JsonProcessingException e) {
                throw new CustomException(ErrorException.FAIL_JSONPROCESSING);
            }
        }
        this.updatedAt = LocalDateTime.now();
    }

    public List<CareerDto> getCareer() {
        return CareerDto.fromJson(this.career);
    }

    // 리프레시 토큰 업데이트
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //비밀번호 재설정
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    //이미지 업데이트
    public void updateProfileImage(String profileImg) {
        this.profileImg = profileImg;
    }

    //회원가입 유저 정보 저장
    public UserEntity(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
