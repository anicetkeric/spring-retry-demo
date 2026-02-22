package com.bootlabs.demo.service;


import com.bootlabs.demo.client.BankApiClient;
import com.bootlabs.demo.exception.BankApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final BankApiClient bankApiClient;

    public PaymentService(BankApiClient bankApiClient) {
        this.bankApiClient = bankApiClient;
    }

    /**
     * Attempts to charge the bank for an order.
     *
     * @Retryable configuration:
     *   - retryFor:  Only retry on BankApiException (transient errors)
     *   - maxAttempts: Try up to 3 times total (1 initial + 2 retries)
     *   - backoff: Wait 2 seconds between attempts (fixed delay)
     */
    @Retryable(
            retryFor  = {BankApiException.class},
            maxAttempts = 3,
            backoff   = @Backoff(delay = 2000)  // 2-second fixed delay
    )
    public String processPayment(String orderId, double amount) {
        log.info("[PaymentService] Attempting payment — Order: {}, Amount: ${}",
                orderId, amount);

        String transactionId = bankApiClient.charge(orderId, amount);

        log.info("[PaymentService] Payment SUCCESS — TxnId: {}", transactionId);
        return transactionId;
    }

    /**
     * Called automatically when ALL retry attempts are exhausted.
     * Rules for @Recover:
     *  1. Must be in the same class as @Retryable
     *  2. Return type must match the @Retryable method
     *  3. First parameter must be the exception type
     *  4. Remaining parameters must match @Retryable method signature
     */
    @Recover
    public String recoverPayment(BankApiException ex, String orderId, double amount) {
        log.error("[PaymentService] ALL RETRIES EXHAUSTED for order: {}. Reason: {}",
                orderId, ex.getMessage());

        // In production: save to a dead-letter queue, trigger manual review,
        //                 or schedule for async reprocessing
        return "PAYMENT_FAILED_PENDING_REVIEW";
    }
}