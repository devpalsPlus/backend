package hs.kr.backend.devpals.domain.project.repository;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findAll();

    @Query("SELECT p FROM ProjectEntity p WHERE p.recruitmentEndDate = :tomorrow AND p.isDone = false")
    List<ProjectEntity> findProjectsEndingTomorrow(@Param("tomorrow") LocalDate tomorrow);
    long count();
    long countByIsDoneFalse();
}
