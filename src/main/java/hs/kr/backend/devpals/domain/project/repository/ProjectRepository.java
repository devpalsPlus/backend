package hs.kr.backend.devpals.domain.project.repository;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
