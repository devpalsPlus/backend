package hs.kr.backend.devpals.domain.faq.repository;

import hs.kr.backend.devpals.domain.faq.entity.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Long> {
    List<FaqEntity> findAllByOrderByCreatedAtDesc();
    List<FaqEntity> findByTitleContainingOrderByCreatedAtDesc(String keyword);

}
