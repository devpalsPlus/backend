package hs.kr.backend.devpals.domain.project.repository;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM ProjectEntity p WHERE p.recruitmentEndDate = :tomorrow AND p.isDone = false")
    List<ProjectEntity> findProjectsEndingTomorrow(@Param("tomorrow") LocalDate tomorrow);

    @Query("SELECT p FROM ProjectEntity p WHERE p.userId = :userId")
    List<ProjectEntity> findProjectsByUserId(@Param("userId") Long userId);
    long count();
    long countByIsDoneFalse();
}
