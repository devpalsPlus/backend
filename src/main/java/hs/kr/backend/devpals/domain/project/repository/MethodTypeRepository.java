package hs.kr.backend.devpals.domain.project.repository;

import hs.kr.backend.devpals.domain.project.entity.MethodTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MethodTypeRepository extends JpaRepository<MethodTypeEntity, Long> {
}
