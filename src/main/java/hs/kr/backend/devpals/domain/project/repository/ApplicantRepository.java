package hs.kr.backend.devpals.domain.project.repository;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<ApplicantEntity, Long> {
    Optional<ApplicantEntity> findByUserAndProject(UserEntity user, ProjectEntity project);
    List<ApplicantEntity> findByUser(UserEntity user);
    @Query("SELECT a FROM ApplicantEntity a " +
            "JOIN FETCH a.user " +
            "JOIN FETCH a.project " +
            "WHERE a.project = :project")
    List<ApplicantEntity> findByProject(@Param("project") ProjectEntity project);

    Optional<ApplicantEntity> findByProjectIdAndUserId(Long projectId, Long evaluateeId);

    List<ApplicantEntity> findAllByProjectIdAndStatus(Long projectId, ApplicantStatus applicantStatus);
}
