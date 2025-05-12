package hs.kr.backend.devpals.domain.evaluation.entity;

import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "evaluation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"projectId", "evaluatorId", "evaluateeId"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private Long evaluatorId;

    @Column(nullable = false)
    private Long evaluateeId;

    @ElementCollection
    @CollectionTable(name = "evaluation_scores", joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "score", nullable = false)
    private List<Integer> scores;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "validatedBuilder")
    public static EvaluationEntity create(Long projectId, Long evaluatorId, Long evaluateeId, List<Integer> scores) {
        if (scores == null || scores.size() != 6) {
            throw new CustomException(ErrorException.INVALID_EVALUATION_SCORES);
        }
        return EvaluationEntity.builder()
                .projectId(projectId)
                .evaluatorId(evaluatorId)
                .evaluateeId(evaluateeId)
                .scores(scores)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
