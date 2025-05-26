package hs.kr.backend.devpals.domain.user.service;

import hs.kr.backend.devpals.domain.Inquiry.entity.InquiryEntity;
import hs.kr.backend.devpals.domain.project.entity.*;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.report.entity.ReportEntity;
import hs.kr.backend.devpals.domain.user.entity.alarm.*;
import hs.kr.backend.devpals.domain.user.entity.UserEntity;
import hs.kr.backend.devpals.domain.user.repository.AlarmRepository;
import hs.kr.backend.devpals.domain.user.repository.UserRepository;
import hs.kr.backend.devpals.global.common.ApiResponse;
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
import java.time.LocalDateTime;
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


    //테스트용 로직입니다 추후 삭제하겠습니다.
    public ResponseEntity<ApiResponse<String>> sendAlarmTest(String token, Integer alarmFilterId){
        Long userId = jwtTokenValidator.getUserId(token);
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
        sendToUserTest(userId, testMessage,alarmFilterId,targetId);
        return new ResponseEntity<>(new ApiResponse<String>(200, true, "ok","테스트 메시지 입니다. 메시지는 따로 저장되지 않습니다."), HttpStatus.OK);
    }

    // 프로젝트 지원 결과 알람 전송
    public void sendAlarm(List<ApplicantEntity> applicants, ProjectEntity project){
        List<ApplicantAlarmEntity> alarmEntities = applicants.stream()
                .map(a -> new ApplicantAlarmEntity(a, makeMessage(a),project))
                .toList();

        List<ApplicantAlarmEntity> savedAlarmEntities = alarmRepository.saveAll(alarmEntities);

        savedAlarmEntities.forEach(a ->
                sendToUser(a.getReceiver().getId(), a)
        );
    }
    //프로젝트 지원 신청시 프로젝트 작성자에게 알림전송
    public void sendAlarm(ProjectEntity project, ApplicantEntity applicant) {
        UserEntity author = userRepository.findById(project.getUserId()).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        String content = makeMessage(project, applicant);
        AlarmEntity saved = alarmRepository.save(new ProjectAlarmEntity(project, author, content,applicant));
        sendToUser(author.getId(),saved);
    }

    //대댓글 작성시 댓글 작성자에게 알림 전송
    public void sendAlarm(RecommentEntity recomment, CommentEntity comment, ProjectEntity project) {
        String content = makeMessage(project, recomment);
        UserEntity receiver = comment.getUser();
        CommentAlarmEntity commentAlarmEntity = new CommentAlarmEntity(comment, content, project, recomment,receiver);
        AlarmEntity saved = alarmRepository.save(commentAlarmEntity);
        sendToUser(receiver.getId(),saved);
    }


    //댓글 작성시 프로젝트 작성자에게 알림 전송
    public void sendAlarm(CommentEntity comment, ProjectEntity project, UserEntity commenter) {
        String content = makeMessage(project, commenter);
        UserEntity receiver = userRepository.findById(project.getUserId()).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        CommentAlarmEntity commentAlarmEntity = new CommentAlarmEntity(comment, content, project, receiver);
        AlarmEntity saved = alarmRepository.save(commentAlarmEntity);
        sendToUser(project.getUserId(),saved);
    }

    public void sendReportAlarm(UserEntity receiver,ReportEntity report) {
        String message = makeReportMessage(receiver.getWarning());
        ReportAlarmEntity reportAlarmEntity = new ReportAlarmEntity(receiver, message, report);
        alarmRepository.save(reportAlarmEntity);
        sendToUser(receiver.getId(),reportAlarmEntity);
    }
    public void sendReportAlarm(ProjectEntity project,ReportEntity report) {
        String message = makeReportMessage(project.getWarning());
        UserEntity receiver = userRepository.findById(project.getUserId()).orElseThrow(() -> new CustomException(ErrorException.USER_NOT_FOUND));
        ReportAlarmEntity reportAlarmEntity = new ReportAlarmEntity(project, receiver,message, report);
        alarmRepository.save(reportAlarmEntity);
        sendToUser(receiver.getId(),reportAlarmEntity);
    }

    public void sendReportAlarm(CommentEntity comment,ReportEntity report) {
        String message = makeReportMessage(comment.getWarning());
        ReportAlarmEntity reportAlarmEntity = new ReportAlarmEntity(comment,message, report);
        alarmRepository.save(reportAlarmEntity);
        sendToUser(comment.getUser().getId(),reportAlarmEntity);
    }
    public void sendReportAlarm(RecommentEntity recomment,ReportEntity report) {
        String message = makeReportMessage(recomment.getWarning());
        ReportAlarmEntity reportAlarmEntity = new ReportAlarmEntity(recomment,message, report);
        alarmRepository.save(reportAlarmEntity);
        sendToUser(recomment.getUser().getId(),reportAlarmEntity);
    }

    private String makeReportMessage(Integer warning) {
        return "신고횟수누적으로 "+ warning+"차 경고처리 되었습니다.";
    }

    private String makeMessage(ProjectEntity project,ApplicantEntity applicant) {
        return "모집중인 '"+project.getTitle()+ "'에 " + applicant.getUser().getNickname() + " 님이 지원하셨습니다.";
    }

    private String makeMessage(ProjectEntity project,UserEntity commenter) {
        return "'"+project.getTitle()+ "'에 " + commenter.getNickname() + " 님의 댓글이 달렸습니다";
    }

    private String makeMessage(ApplicantEntity applicant) {
        String result = applicant.getStatus().equals(ApplicantStatus.ACCEPTED) ? "합격" : "불합격";

        return "내가 지원한 '"+applicant.getProject().getTitle()+"'에 "+ result +"하셨습니다.";
    }

    private String makeMessage(ProjectEntity project, RecommentEntity recomment) {
        return "'"+project.getTitle()+ "'에 작성자의 답변이 달렸습니다.";
    }


    // 특정 사용자에게 알림 전송
    private void sendToUser(Long userId, AlarmEntity alarmEntity) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(Map.of(
                                "message", alarmEntity.getContent(),
                                "createAt", alarmEntity.getCreatedAt(),
                                "alarmFilterId", alarmEntity.getAlarmFilterIntValue(),
                                "routingId", alarmEntity.getRoutingId()
                        )));
            } catch (IOException e) {
                log.error("Error sending SSE to user {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }

    // 테스트용 로직입니다 추후 삭제하겠습니다.
    private void sendToUserTest(Long userId, String message, Integer alarmFilterValue,Long routingId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(Map.of(
                                "message", message,
                                "createAt", LocalDateTime.now(),
                                "alarmFilterId",alarmFilterValue,
                                "routingId",routingId
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
