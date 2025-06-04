package hs.kr.backend.devpals.domain.tag.repository;

import hs.kr.backend.devpals.domain.tag.entity.PositionTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionTagRepository extends JpaRepository<PositionTagEntity, Long> {
}
