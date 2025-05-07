package hs.kr.backend.devpals.domain.evaluation.repository;

import hs.kr.backend.devpals.domain.evaluation.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository  extends JpaRepository<EvaluationEntity, Long> {

    boolean existsByProjectIdAndEvaluatorIdAndEvaluateeId(Long projectId, Long evaluatorId, Long evaluateeId);
}
