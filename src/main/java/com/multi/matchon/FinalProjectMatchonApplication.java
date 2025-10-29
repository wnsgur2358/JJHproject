package com.multi.matchon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing(auditorAwareRef = "loginUserAuditorAware")
@SpringBootApplication
@EnableScheduling
public class FinalProjectMatchonApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProjectMatchonApplication.class, args);
    }

}
