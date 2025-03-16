package hs.kr.backend.devpals.global.schedule;

import hs.kr.backend.devpals.domain.project.entity.ProjectEntity;
import hs.kr.backend.devpals.domain.project.repository.ProjectRepository;
import hs.kr.backend.devpals.domain.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectScheduleService {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;


    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void closeExpiredProjects() {
        log.info("스케줄러 시작: {}", LocalDateTime.now());

        List<ProjectEntity> projectsEndingTomorrow =
                projectRepository.findProjectsEndingTomorrow(LocalDate.now().minusDays(1));

        CompletableFuture.runAsync(() -> {
            log.info("프로젝트 종료 작업 시작: {}", LocalDateTime.now());
            projectService.closeProject(projectsEndingTomorrow);
            log.info("프로젝트 종료 작업 완료: {}", LocalDateTime.now());
        });

        log.info("스케줄러 종료: {}", LocalDateTime.now());
    }
}
