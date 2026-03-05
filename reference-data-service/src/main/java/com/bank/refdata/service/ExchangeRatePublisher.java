package com.bank.refdata.service;

import com.bank.schema.ExchangeRate;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
public class ExchangeRatePublisher {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRatePublisher.class);
    private static final String TOPIC = "currency-rates";

    private final KafkaTemplate<String, ExchangeRate> kafkaTemplate;
    private final Random random = new Random();

    public ExchangeRatePublisher(KafkaTemplate<String, ExchangeRate> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // This Bean automatically creates the topic with Log Compaction enabled!
    @Bean
    public NewTopic currencyRatesTopic() {
        return TopicBuilder.name(TOPIC)
                .partitions(3)
                .replicas(1)
                .compact() // <-- This is the banking magic!
                .build();
    }

    // Runs every 5 seconds to simulate a live market feed
    @Scheduled(fixedRate = 5000)
    public void publishRates() {
        List<String> pairs = List.of("USD_EUR", "USD_INR", "EUR_INR");

        for (String pair : pairs) {
            // Create a slight fluctuation in the base rate
            double simulatedRate = getBaseRate(pair) + (random.nextDouble() * 0.05);

            ExchangeRate rateEvent = ExchangeRate.newBuilder()
                    .setCurrencyPair(pair)
                    .setRate(simulatedRate)
                    .setTimestamp(Instant.now().toEpochMilli())
                    .build();

            // The 'key' (pair) is crucial for log compaction to know what to overwrite
            kafkaTemplate.send(TOPIC, pair, rateEvent);
            log.info("Published updated rate: {} -> {}", pair, simulatedRate);
        }
    }

    private double getBaseRate(String pair) {
        return switch (pair) {
            case "USD_EUR" -> 0.92;
            case "USD_INR" -> 83.10;
            case "EUR_INR" -> 90.20;
            default -> 1.0;
        };
    }
}