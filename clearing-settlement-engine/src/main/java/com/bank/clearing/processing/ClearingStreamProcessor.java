package com.bank.clearing.processing;

import com.bank.schema.ClearedPayment;
import com.bank.schema.ExchangeRate;
import com.bank.schema.PaymentEvent;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ClearingStreamProcessor {

    private static final Logger log = LoggerFactory.getLogger(ClearingStreamProcessor.class);

    @Autowired
    public void buildPipeline(StreamsBuilder builder) {

        // 1. Load the live exchange rates into a lightning-fast memory table
        GlobalKTable<String, ExchangeRate> exchangeRatesTable = builder.globalTable("currency-rates");

        // 2. Connect to the firehose of raw incoming payments
        KStream<String, PaymentEvent> rawPaymentsStream = builder.stream("payments-raw");

        // 3. The Real-Time Join & Math
        rawPaymentsStream
                // We will settle all payments in INR for this engine.
                // This extracts the currency from the payment (e.g., "USD") and appends "_INR" to look up the rate.
                .leftJoin(exchangeRatesTable,
                        (paymentId, paymentEvent) -> paymentEvent.getCurrency() + "_INR",
                        this::calculateSettlement)
                // 4. Output the results
                .peek((key, clearedPayment) -> log.info("Successfully cleared payment {} for {} INR",
                        clearedPayment.getTransactionId(), clearedPayment.getSettledAmount()))
                .to("payments-cleared");
    }

    private ClearedPayment calculateSettlement(PaymentEvent payment, ExchangeRate rateInfo) {
        // If the currency is already INR, or we can't find a rate, default to 1.0
        double appliedRate = (rateInfo != null) ? rateInfo.getRate() : 1.0;
        double finalSettledAmount = payment.getAmount() * appliedRate;

        return ClearedPayment.newBuilder()
                .setTransactionId(payment.getTransactionId())
                .setSourceAccountId(payment.getSourceAccountId())
                .setDestinationAccountId(payment.getDestinationAccountId())
                .setOriginalAmount(payment.getAmount())
                .setOriginalCurrency(payment.getCurrency())
                .setAppliedExchangeRate(appliedRate)
                .setSettledAmount(finalSettledAmount)
                .setStatus("CLEARED")
                .setTimestamp(Instant.now().toEpochMilli())
                .build();
    }
}