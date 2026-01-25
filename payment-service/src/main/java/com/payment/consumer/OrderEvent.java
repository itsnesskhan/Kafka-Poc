package com.payment.consumer;

import com.payment.dto.OrderDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderEvent implements Serializable {

    private String orderId;
    private String userId;
    private String email;
    private Double amount;
    private String status;   // CREATED
    private LocalDateTime createdAt;

    public OrderEvent() {}

    public OrderEvent(OrderDTO order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUserId();
        this.email = order.getEmail();
        this.amount = order.getAmount();
        this.status = order.getStatus();
        this.createdAt = LocalDateTime.now();
    }

}
