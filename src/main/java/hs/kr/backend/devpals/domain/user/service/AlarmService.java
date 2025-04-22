package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.project.entity.ApplicantEntity;
import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.user.dto.UserAlarmDto;
import hs.kr.backend.devpals.domain.user.entity.AlramEntity;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.AlarmRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
import hs.kr.backend.devpals.global.common.enums.AlramFilter;
import hs.kr.backend.devpals.global.common.enums.ApplicantStatus;
import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import hs.kr.backend.devpals.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final JwtTokenValidator jwtTokenValidator;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;


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
    public void sendAlarm(List<ApplicantEntity> applicants, AlramFilter alramFilter,Long targetId){
        List<AlramEntity> alramEntities = applicants.stream()
                .map(a -> new AlramEntity(a, makeMessage(a), alramFilter))
                .toList();

        alarmRepository.saveAll(alramEntities);

        alramEntities.forEach(a ->
                sendToUser(UserAlarmDto.of(a.getUser()), a.getContent(),alramFilter.getValue(),targetId)
        );
    }

    //테스트용 로직입니다 추후 삭제하겠습니다.
    public ResponseEntity<ApiResponse<String>> sendAlarmTest(String token, Integer alarmFilterId){
        Long userId = jwtTokenValidator.getUserId(token);
        UserAlarmDto userAlarmDto =  UserAlarmDto.of(userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND)));
        String testMessage ="테스트 메시지 입니다. 메시지는 따로 저장되지 않습니다. " +
                "전달되는 targetId의 경우 alarmFilter 값에 따라 0(전체), 1(지원한 프로젝트),2(지원자 확인)의 경우 아무 ProjectId," +
                "3의경우 미구현이라 현재 유저 ID전달드리겠습니다.";
        Long targetId;
        if(alarmFilterId >=2) {
            ProjectEntity first = projectRepository.findAll().stream().findFirst().orElseThrow(() ->
                    new CustomException(ErrorException.PROJECT_NOT_FOUND)
            );
            targetId = first.getId();
        } else targetId = userId;
        sendToUser(userAlarmDto, testMessage,alarmFilterId,targetId);
        return new ResponseEntity<>(new ApiResponse<String>(true, "ok","테스트 메시지 입니다. 메시지는 따로 저장되지 않습니다."), HttpStatus.OK);
    }

    //프로젝트 지원 신청시 프로젝트 작성자에게 알림전송
    public void sendAlarm(ProjectEntity project, ApplicantEntity applicant, AlramFilter alramFilter) {
        UserEntity author = userRepository.findById(project.getAuthorId()).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        String content = makeMessage(project, applicant);
        alarmRepository.save(new AlramEntity(project,author,content,alramFilter));
        sendToUser(UserAlarmDto.of(author),content,alramFilter.getValue(),project.getId());
    }

    private String makeMessage(ProjectEntity project,ApplicantEntity applicant) {
        return project.getTitle()+" 프로젝트에 "+ applicant.getUser().getNickname() + "님이 지원하셨습니다.";
    }

    private String makeMessage(ApplicantEntity applicant) {
        String result = applicant.getStatus().equals(ApplicantStatus.ACCEPTED) ? "합격" : "불합격";

        return "지원자님은 "+applicant.getProject().getTitle()+" 프로젝트에 "+ result +" 하셨습니다.";
    }

    // 특정 사용자에게 알림 전송
    private void sendToUser(UserAlarmDto userAlarmDto, String message,Integer alarmFilterValue,Long targetId) {
        SseEmitter emitter = emitters.get(userAlarmDto.getUserId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(Map.of(
                                "message", message,
                                "nickName",userAlarmDto.getNickName(),
                                "alarmFilterId",alarmFilterValue,
                                "targetId",targetId
                        )));
            } catch (IOException e) {
                log.error("Error sending SSE to user {}: {}", userAlarmDto.getUserId(), e.getMessage());
                emitters.remove(userAlarmDto.getUserId());
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
