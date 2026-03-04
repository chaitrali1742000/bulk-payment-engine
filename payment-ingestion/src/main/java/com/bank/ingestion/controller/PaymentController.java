package com.bank.ingestion.controller;

import com.bank.ingestion.dto.PaymentRequest;
import com.bank.ingestion.service.PaymentProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentProducer paymentProducer;

    public PaymentController(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @PostMapping
    public ResponseEntity<String> ingestPayment(@RequestBody PaymentRequest request) {
        paymentProducer.publishPayment(request);
        // Return a fast 202 Accepted. We don't make the user wait for Kafka to finish processing.
        return ResponseEntity.accepted().body("Payment accepted for processing: " + request.transactionId());
    }
}