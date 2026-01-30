package com.order.controller;

import com.order.entity.Order;
import com.order.producer.OrderProducer;
import com.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;

    private final OrderService orderService;

    @PostMapping("v1/orders")
    public ResponseEntity<Order> createOrderAsync(@RequestBody Order order) {
        orderProducer.createOrder(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }


    @PostMapping("v2/orders")
    @CircuitBreaker(name = "paymentServiceCB", fallbackMethod = "paymentFallback")
    public ResponseEntity<Order> createOrderSync(@RequestBody Order order) {
        order = orderService.createOrder(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }


    public ResponseEntity<Map<String, Object>> paymentFallback(
            Order order, Throwable ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", "PAYMENT_SERVICE_DOWN");
        response.put("message", "Payment service is temporarily unavailable");
        response.put("action", "Order created, payment pending");
        response.put("orderId", order.getOrderId());
        response.put("retryAfter", 10);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }


}

