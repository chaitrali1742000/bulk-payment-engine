package com.bank.refdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReferenceDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReferenceDataApplication.class, args);
    }
}