package com.samyookgoo.palgoosam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PalgoosamApplication {

    public static void main(String[] args) {
        SpringApplication.run(PalgoosamApplication.class, args);
    }

}
