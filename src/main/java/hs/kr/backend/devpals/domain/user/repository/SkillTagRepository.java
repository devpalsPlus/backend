package hs.kr.backend.devpals.domain.user.repository;

import hs.kr.backend.devpals.domain.user.entity.SkillTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillTagRepository extends JpaRepository<SkillTagEntity, Long> {
    List<SkillTagEntity> findByNameIn(List<String> names);
}
