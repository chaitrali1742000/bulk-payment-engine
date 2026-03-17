package com.bank.clearing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class ClearingEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClearingEngineApplication.class, args);
    }
}