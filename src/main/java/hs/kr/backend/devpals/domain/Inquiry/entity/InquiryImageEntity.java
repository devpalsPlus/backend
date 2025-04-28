package hs.kr.backend.devpals.domain.Inquiry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "InquiryImages")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiryId", nullable = false)
    private InquiryEntity inquiry;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static InquiryImageEntity from(InquiryEntity inquiry, String imageUrl) {
        return InquiryImageEntity.builder()
                .inquiry(inquiry)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
