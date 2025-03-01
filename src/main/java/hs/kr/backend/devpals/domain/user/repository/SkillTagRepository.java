package hs.kr.backend.devpals.domain.user.repository;

import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillTagRepository extends JpaRepository<SkillTagEntity, Long> {
}
