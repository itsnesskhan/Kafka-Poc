package com.order.controller;

import com.order.entity.Order;
import com.order.enums.ORDER_STATUS;
import com.order.producer.OrderProducer;
import com.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createOrderSync(@RequestBody Order order) {

        Order savedOrder = orderService.createOrder(order);

        if (ORDER_STATUS.PAYMENT_PENDING.getValue().equals(savedOrder.getStatus())) {
            return ResponseEntity
                    .accepted()
                    .header("Location", "/v2/orders/" + savedOrder.getOrderId())
                    .body(Map.of(
                            "orderId", savedOrder.getOrderId(),
                            "status", "PAYMENT_PENDING",
                            "message", "Payment service unavailable. Will retry asynchronously."
                    ));

        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedOrder);
    }
}

