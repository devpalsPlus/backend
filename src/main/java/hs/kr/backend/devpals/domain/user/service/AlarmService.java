package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.user.entity.AlramEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.AlarmRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final JwtTokenValidator jwtTokenValidator;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;


    // 새 SSE 연결 생성
    public SseEmitter createEmitter(String token) {
        Long userId = jwtTokenValidator.getUserId(token);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, emitter);
        return emitter;
    }

    // 지원 결과 알람 전송
    public void sendAlarm(List<ApplicantEntity> applicants, AlramFilter alramFilter){
        List<AlramEntity> alramEntities = applicants.stream()
                .map(a -> new AlramEntity(a, makeMessage(a), alramFilter))
                .toList();

        alarmRepository.saveAll(alramEntities);

        alramEntities.forEach(a ->
                sendToUser(a.getUser().getId(), a.getContent())
        );
    }

    //프로젝트 지원 신청시 프로젝트 작성자에게 알림전송
    public void sendAlarm(ProjectEntity project, ApplicantEntity applicant, AlramFilter alramFilter) {
        UserEntity author = userRepository.findById(project.getAuthorId()).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        String content = makeMessage(project, applicant);
        alarmRepository.save(new AlramEntity(project,author,content,alramFilter));
        sendToUser(project.getAuthorId(),content);
    }

    private String makeMessage(ProjectEntity project,ApplicantEntity applicant) {
        return project.getTitle()+" 프로젝트에 "+ applicant.getUser().getNickname() + "님이 지원하셨습니다.";
    }

    private String makeMessage(ApplicantEntity applicant) {
        String result = applicant.getStatus().equals(ApplicantStatus.ACCEPTED) ? "합격" : "불합격";

        return "지원자님은 "+applicant.getProject().getTitle()+" 프로젝트에 "+ result +" 하셨습니다.";
    }

    // 특정 사용자에게 알림 전송
    private void sendToUser(Long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(Map.of(
                                "message", message
                        )));
            } catch (IOException e) {
                log.error("Error sending SSE to user {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }

    //연결 확인후 문제 있을시 연결 제거
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void cleanupExpiredEmitters() {
        emitters.entrySet().removeIf(entry -> {
            SseEmitter emitter = entry.getValue();
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
                return false;
            } catch (IOException e) {
                return true;
            }
        });
    }


}
