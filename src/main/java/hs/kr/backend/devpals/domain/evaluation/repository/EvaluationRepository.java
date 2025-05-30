package hs.kr.backend.devpals.domain.evaluation.repository;

import hs.kr.backend.devpals.domain.evaluation.entity.EvaluationEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository  extends JpaRepository<EvaluationEntity, Long> {

    boolean existsByProjectIdAndEvaluatorIdAndEvaluateeId(Long projectId, Long evaluatorId, Long evaluateeId);

    List<EvaluationEntity> findAllByEvaluateeId(Long userId);

    Optional<EvaluationEntity> findByProjectIdAndEvaluatorIdAndEvaluateeId(Long projectId, Long evaluatorId, Long evaluateeId);
}
