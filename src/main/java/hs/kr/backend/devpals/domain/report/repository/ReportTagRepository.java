package hs.kr.backend.devpals.domain.report.repository;

import hs.kr.backend.devpals.domain.report.entity.ReportTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTagRepository extends JpaRepository<ReportTagEntity, Long>{
}
