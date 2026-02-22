package com.bootlabs.demo.controller;

import com.bootlabs.demo.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestParam String customerId, @RequestParam double amount) {

        String result = orderService.placeOrder(customerId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}