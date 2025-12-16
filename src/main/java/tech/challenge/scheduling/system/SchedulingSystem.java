package tech.challenge.scheduling.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulingSystem {

    public static void main(String[] args) {
        SpringApplication.run(SchedulingSystem.class, args);
    }

}
