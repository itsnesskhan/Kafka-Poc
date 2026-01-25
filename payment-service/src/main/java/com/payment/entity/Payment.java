package com.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String paymentId;
    private String orderId;
    private Double amount;
    private String status; // SUCCESS / FAILED
}

