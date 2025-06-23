package hs.kr.backend.devpals.domain.Inquiry.repository;

import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    int countByUserId(Long userId);
    List<InquiryEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<InquiryEntity> findAllByOrderByCreatedAtDesc();
    List<InquiryEntity> findInquiriesByUserIdAndDate(Long userId, LocalDateTime start, LocalDateTime end);
    List<InquiryEntity> findByUser(UserEntity user);


    @Query("SELECT i FROM InquiryEntity i WHERE i.createdAt BETWEEN :startDate AND :endDate ORDER BY i.createdAt DESC")
    List<InquiryEntity> findInquiriesByDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
