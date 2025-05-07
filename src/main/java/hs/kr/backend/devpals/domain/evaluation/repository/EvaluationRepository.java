package hs.kr.backend.devpals.domain.evaluation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository  extends JpaRepository<Evaluaiton, Long> {

}
