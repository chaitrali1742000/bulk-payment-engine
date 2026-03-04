package com.bank.ingestion.dto;

public record PaymentRequest(
        String transactionId,
        String bulkFileId,
        String sourceAccountId,
        String destinationAccountId,
        double amount,
        String currency
) {
}
