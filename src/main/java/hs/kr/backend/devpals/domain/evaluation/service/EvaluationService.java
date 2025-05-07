package hs.kr.backend.devpals.domain.evaluation.service;

import hs.kr.backend.devpals.domain.evaluation.dto.EvaluationMemberResponse;
import hs.kr.backend.devpals.domain.evaluation.dto.EvaluationRequest;
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
import java.util.List;
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

        return ResponseEntity.ok(new ApiResponse<>(true, "평가가 성공적으로 제출되었습니다.", null));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<EvaluationMemberResponse>>> getProjectMembersWithEvaluationStatus(
            String token, Long projectId) {

        Long evaluatorId = jwtTokenValidator.getUserId(token);

        //프로젝트에 참여 중인 유저 리스트 조회
        List<ApplicantEntity> acceptedApplicants = applicantRepository.findAllByProjectIdAndStatus(projectId, ApplicantStatus.ACCEPTED);

        //평가 여부 판별
        List<EvaluationMemberResponse> responseList = acceptedApplicants.stream()
                .map(applicant -> {
                    UserEntity user = applicant.getUser();
                    boolean isEvaluated = evaluationRepository.existsByProjectIdAndEvaluatorIdAndEvaluateeId(
                            projectId, evaluatorId, user.getId());
                    return EvaluationMemberResponse.of(user.getId(), user.getNickname(), isEvaluated);
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "참여자 조회 성공", responseList));
    }


}
