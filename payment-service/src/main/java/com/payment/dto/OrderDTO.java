package com.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderDTO {

    private String orderId;
    private String userId;
    private String email;
    private Double amount;
    private String status; // CREATED
}
