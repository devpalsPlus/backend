package hs.kr.backend.devpals.domain.Inquiry.repository;

import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    int countByUserId(Long userId);
    List<InquiryEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<InquiryEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT i FROM InquiryEntity i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY i.createdAt DESC")
    List<InquiryEntity> searchByTitle(@Param("keyword") String keyword);

}
