package hs.kr.backend.devpals.domain.evaluation.service;

import hs.kr.backend.devpals.domain.evaluation.dto.EvaluationMemberResponse;
import hs.kr.backend.devpals.domain.evaluation.dto.EvaluationRequest;
import hs.kr.backend.devpals.domain.evaluation.dto.EvaluationResponse;
import hs.kr.backend.devpals.domain.evaluation.entity.EvaluationEntity;
import hs.kr.backend.devpals.domain.evaluation.repository.EvaluationRepository;
import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.repository.ApplicantRepository;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final JwtTokenValidator jwtTokenValidator;
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final ApplicantRepository applicantRepository;

    @Transactional
    public ResponseEntity<ApiResponse<String>> submitEvaluations(String token, EvaluationRequest request) {
        Long evaluatorId = jwtTokenValidator.getUserId(token);

        userRepository.findById(evaluatorId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));

        Long projectId = request.getProjectId();
        Long evaluateeId = request.getEvaluateeId();

        //중복 평가 방지
        if (evaluationRepository.existsByProjectIdAndEvaluatorIdAndEvaluateeId(projectId, evaluatorId, evaluateeId)) {
            throw new CustomException(ErrorException.EVALUATION_ALREADY_EXISTS);
        }

        ApplicantEntity applicant = applicantRepository.findByProjectIdAndUserId(projectId, evaluateeId)
                .orElseThrow(() -> new CustomException(ErrorException.APPLICANT_NOT_FOUND));

        if (!applicant.isAccepted()) {
            throw new CustomException(ErrorException.INVALID_EVALUATION_TARGET);
        }

        EvaluationEntity evaluation = EvaluationEntity.create(
                projectId, evaluatorId, evaluateeId, request.getScores()
        );

        evaluationRepository.save(evaluation);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "평가가 성공적으로 제출되었습니다.", null));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<EvaluationResponse>> getProjectMembersWithEvaluationStatus(
            String token, Long projectId) {

        Long evaluatorId = jwtTokenValidator.getUserId(token);

        // 프로젝트 참여자 조회
        List<ApplicantEntity> acceptedApplicants = applicantRepository.findAllByProjectIdAndStatus(projectId, ApplicantStatus.ACCEPTED);

        // 프로젝트 이름은 참여자 중 아무거나에서 꺼냄
        String projectName = acceptedApplicants.stream()
                .findFirst()
                .map(applicant -> applicant.getProject().getTitle())
                .orElse("알 수 없는 프로젝트");

        // evaluator 자신 제외 + 응답 생성
        List<EvaluationMemberResponse> userData = acceptedApplicants.stream()
                .filter(applicant -> !applicant.getUser().getId().equals(evaluatorId))
                .map(applicant -> {
                    UserEntity user = applicant.getUser();
                    boolean isEvaluated = evaluationRepository.existsByProjectIdAndEvaluatorIdAndEvaluateeId(
                            projectId, evaluatorId, user.getId());
                    return EvaluationMemberResponse.of(user.getId(), user.getNickname(), isEvaluated);
                })
                .collect(Collectors.toList());

        EvaluationResponse response = EvaluationResponse.of(projectName, userData);

        return ResponseEntity.ok(new ApiResponse<>(200, true, "참여자 조회 성공", response));
    }

    public List<Integer> calculateAverageScores(Long userId) {
        List<EvaluationEntity> evaluations = evaluationRepository.findAllByEvaluateeId(userId);

        if (evaluations.isEmpty()) return List.of(0, 0, 0, 0, 0, 0);

        int categoryCount = 6;
        int[] scoreSums = new int[categoryCount];
        int[] scoreCounts = new int[categoryCount];

        for (EvaluationEntity evaluation : evaluations) {
            List<Integer> scores = evaluation.getScores();
            for (int i = 0; i < scores.size(); i++) {
                scoreSums[i] += scores.get(i);
                scoreCounts[i]++;
            }
        }

        List<Integer> averages = new ArrayList<>();
        for (int i = 0; i < categoryCount; i++) {
            if (scoreCounts[i] == 0) {
                averages.add(0);
            } else {
                averages.add(scoreSums[i] / scoreCounts[i]);
            }
        }

        return averages;
    }

    public boolean isAllEvaluated(Long projectId) {
        List<ApplicantEntity> acceptedApplicants = applicantRepository.findByProjectIdAndStatus(
                projectId, ApplicantStatus.ACCEPTED
        );

        if (acceptedApplicants.size() <= 1) return false;

        List<Long> participantIds = acceptedApplicants.stream()
                .map(applicant -> applicant.getUser().getId())
                .toList();

        int requiredEvaluationCount = participantIds.size() * (participantIds.size() - 1);

        int actualEvaluationCount = evaluationRepository.countByProjectIdAndEvaluatorIdInAndEvaluateeIdIn(
                projectId, participantIds, participantIds
        );

        return actualEvaluationCount == requiredEvaluationCount;
    }

}
