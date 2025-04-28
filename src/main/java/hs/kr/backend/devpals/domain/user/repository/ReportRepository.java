package hs.kr.backend.devpals.domain.user.repository;


import hs.kr.backend.devpals.domain.project.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
}
