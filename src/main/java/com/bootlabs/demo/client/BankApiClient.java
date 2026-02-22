package com.bootlabs.demo.client;

import com.bootlabs.demo.exception.BankApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BankApiClient {

    private static final Logger log = LoggerFactory.getLogger(BankApiClient.class);

    // Simulates a counter to track how many times the bank was called
    private final AtomicInteger callCount = new AtomicInteger(0);

    public String charge(String orderId, double amount) {
        int attempt = callCount.incrementAndGet();
        log.warn("[BankAPI] Attempt #{} — processing payment for order: {}", attempt, orderId);

        // Simulate: first 2 attempts fail, 3rd succeeds
        if (attempt <= 2) {
            log.error("[BankAPI] Attempt #{} FAILED — Bank timeout simulated", attempt);
            throw new BankApiException(
                    "Bank API timeout on attempt " + attempt
            );
        }

        log.info("[BankAPI] Attempt #{} SUCCEEDED — Payment of ${} approved for order: {}",
                attempt, amount, orderId);

        // Reset for next test run
        callCount.set(0);

        return "TXN-" + orderId + "-APPROVED";
    }
}