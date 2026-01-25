package com.order.producer;

import com.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderEvent implements Serializable {

    private String orderId;
    private String userId;
    private String email;
    private Double amount;
    private String status;   // CREATED
    private LocalDateTime createdAt;

    public OrderEvent(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUserId();
        this.email = order.getEmail();
        this.amount = order.getAmount();
        this.status = order.getStatus();
        this.createdAt = LocalDateTime.now();
    }

}
