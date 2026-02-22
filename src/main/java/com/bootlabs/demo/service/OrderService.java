package com.bootlabs.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String placeOrder(String customerId, double amount) {
        String orderId = "ORD-" + System.currentTimeMillis();

        log.info("[OrderService] Saving order {} for customer {} — amount: ${}",
                orderId, customerId, amount);

        // Step 1: Save order (in a real app, persist to DB here)
        log.info("[OrderService] Order {} saved successfully", orderId);

        // Step 2: Process payment — Spring Retry handles failures transparently
        String transactionId = paymentService.processPayment(orderId, amount);

        if (transactionId.startsWith("PAYMENT_FAILED")) {
            log.warn("[OrderService] Payment could not be processed for {}. Queued for review.", orderId);
            return "Order placed but payment pending review. Order ID: " + orderId;
        }

        log.info("[OrderService] Order {} COMPLETED. Transaction: {}", orderId, transactionId);
        return "Order confirmed! Order ID: " + orderId + " | Transaction: " + transactionId;
    }
}