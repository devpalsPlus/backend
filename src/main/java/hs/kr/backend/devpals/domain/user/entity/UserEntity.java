package hs.kr.backend.devpals.domain.user.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.user.convert.LongListConverter;
import hs.kr.backend.devpals.domain.user.dto.CareerDto;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

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

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean beginner;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAdmin = false;

    @Column(length = 255)
    private String github;

    @Column(columnDefinition = "JSON")
    private String career; // JSON 형태의 데이터 저장

    @Column(nullable = false)
    private Integer warning = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Convert(converter = LongListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Long> positionIds = new ArrayList<>();

    @Convert(converter = LongListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Long> skillIds = new ArrayList<>();


    // 내가 신고당한 내역 (새로 추가)
    @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "reportTargetId", referencedColumnName = "id")
    @SQLRestriction("report_filter = 'USER'")  // @Where 대신 @SQLRestriction 사용
    private List<ReportEntity> receivedReports = new ArrayList<>();

    @Column
    private LocalDateTime bannedUntil; // 정지 만료일 (null이면 정지 아님)

    @Column(nullable = false)
    private boolean isPermanentlyBanned = false; // 영구 정지 여부


    // 유저 업데이트
    public void updateUserInfo(String nickname, String bio, Boolean beginner,
                               List<Long> positionIds, List<Long> skillIds,
                               String github, List<Map<String, Object>> career) {
        if (nickname != null) { this.nickname = nickname; }
        if (bio != null) { this.bio = bio; }
        if (beginner != null) { this.beginner = beginner; }
        if (positionIds != null) { this.positionIds = new ArrayList<>(positionIds); }
        if (skillIds != null) { this.skillIds = new ArrayList<>(skillIds); }
        if (github != null) { this.github = github; }
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

    // warning 증가 메서드
    public void increaseWarning() {
        this.warning++;
    }

    //github 주소 업데이트
    public void updateGithub(String github) {this.github = github;}

    //회원가입 유저 정보 저장
    public UserEntity(String email, String password, String nickname, Boolean beginner) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.beginner = beginner;
    }
}
