package com.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentDTO {

    private String paymentId;
    private String orderId;
    private Double amount;
    private String status;
}

