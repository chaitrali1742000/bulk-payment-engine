package com.bank.ingestion.service;

import com.bank.ingestion.dto.PaymentRequest;
import com.bank.schema.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentProducer {

    private static final Logger log = LoggerFactory.getLogger(PaymentProducer.class);
    private static final String TOPIC = "payments-raw";

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPayment(PaymentRequest request) {
        // Translate the incoming JSON request into our strict Avro object
        PaymentEvent event = PaymentEvent.newBuilder()
                .setTransactionId(request.transactionId())
                .setBulkFileId(request.bulkFileId())
                .setSourceAccountId(request.sourceAccountId())
                .setDestinationAccountId(request.destinationAccountId())
                .setAmount(request.amount())
                .setCurrency(request.currency())
                .setStatus("PENDING")
                .setTimestamp(Instant.now().toEpochMilli())
                .build();

        // Send to Kafka using the transactionId as the routing key
        kafkaTemplate.send(TOPIC, event.getTransactionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent payment {} to partition {}",
                                event.getTransactionId(),
                                result.getRecordMetadata().partition());
                    } else {
                        log.error("Failed to send payment {}", event.getTransactionId(), ex);
                    }
                });
    }
}
