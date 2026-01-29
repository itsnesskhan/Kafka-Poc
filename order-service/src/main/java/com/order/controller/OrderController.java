package com.order.controller;

import com.order.entity.Order;
import com.order.producer.OrderProducer;
import com.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Order> createOrderSync(@RequestBody Order order) {
        order = orderService.createOrder(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}

