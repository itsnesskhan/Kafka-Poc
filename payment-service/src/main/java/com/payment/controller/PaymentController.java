package com.payment.controller;

import com.payment.consumer.OrderEvent;
import com.payment.dto.PaymentResponse;
import com.payment.entity.Payment;
import com.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody OrderEvent orderEvent) {

        Payment payment = paymentService.processPayment(orderEvent);

        PaymentResponse response = new PaymentResponse(
                payment.getOrderId(),
                payment.getStatus(),
                payment.getAmount()
        );

        return ResponseEntity.ok(response);
    }
}
