package com.order.controller;

import com.order.entity.Order;
import com.order.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        orderProducer.createOrder(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}

