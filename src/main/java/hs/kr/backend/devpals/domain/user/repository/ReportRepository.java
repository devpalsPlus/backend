package hs.kr.backend.devpals.domain.user.repository;


import hs.kr.backend.devpals.domain.project.entity.ReportEntity;
import hs.kr.backend.devpals.global.common.enums.ReportFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    // 기본 필터링 메서드
    List<ReportEntity> findByReportFilterAndReportTargetId(ReportFilter reportFilter, Long reportTargetId);


    @Query("SELECT r FROM ReportEntity r " +
            "WHERE r.reporter.nickname LIKE %:keyword% " +
            "OR EXISTS (SELECT u FROM UserEntity u WHERE u.nickname LIKE %:keyword%) " +
            "OR EXISTS (SELECT p FROM ProjectEntity p WHERE p.title LIKE %:keyword%) " +
            "OR EXISTS (SELECT c FROM CommentEntity c WHERE c.content LIKE %:keyword%) " +
            "OR EXISTS (SELECT rc FROM RecommentEntity rc WHERE rc.content LIKE %:keyword%) " +
            "OR EXISTS (SELECT iq FROM InquiryEntity iq WHERE iq.content LIKE %:keyword%)")
    List<ReportEntity> searchByKeyword(@Param("keyword") String keyword);
}
