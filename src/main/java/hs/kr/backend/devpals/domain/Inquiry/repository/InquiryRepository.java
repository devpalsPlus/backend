package hs.kr.backend.devpals.domain.Inquiry.repository;

import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    int countByUserId(Long userId);
    List<InquiryEntity> findByUserId(Long userId);
    List<InquiryEntity> findAllByOrderByCreatedAtDesc();
}
