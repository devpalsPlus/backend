package hs.kr.backend.devpals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class DevpalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevpalsApplication.class, args);
	}

}
