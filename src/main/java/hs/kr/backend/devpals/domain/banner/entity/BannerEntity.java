package hs.kr.backend.devpals.domain.banner.entity;

import hs.kr.backend.devpals.domain.banner.dto.BannerRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean visible;

    @Column(nullable = false)
    private boolean always;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void update(BannerRequest request, String newImageUrl) {
        if (newImageUrl != null) {
            this.imageUrl = newImageUrl;
        }
        this.visible = request.isVisible();
        this.always = request.isAlways();
        this.startDate = request.isAlways() ? null : request.getStartDate();
        this.endDate = request.isAlways() ? null : request.getEndDate();
        this.updatedAt = LocalDateTime.now();
    }

    public void setInvisible() {
        this.visible = false;
        this.updatedAt = LocalDateTime.now();
    }
}
