package hs.kr.backend.devpals.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    //EC2 FreeTier에 맞춰 설정
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);  // 최소 1개의 스레드 유지 (EC2 Free Tier에서는 1개가 적절)
        executor.setMaxPoolSize(2);   // 최대 2개의 스레드까지 확장 가능 (CPU 사용 과부하 방지)
        executor.setQueueCapacity(5); // 대기열 크기 (메모리 과부하 방지)
        executor.setKeepAliveSeconds(30); // 30초 후 유휴 스레드 종료
        executor.setThreadNamePrefix("TaskExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);  // 최소 1개의 스레드 유지 (EC2 Free Tier에서는 1개가 적절)
        executor.setMaxPoolSize(2);   // 최대 2개의 스레드 (SMTP 전송 속도를 고려한 최적 설정)
        executor.setQueueCapacity(10); // 대기열 크기 (SMTP 병목 방지)
        executor.setKeepAliveSeconds(30); // 30초 후 유휴 스레드 종료
        executor.setThreadNamePrefix("EmailExecutor-");
        executor.initialize();
        return executor;
    }
}
