package com.payment.service;

import com.payment.consumer.OrderEvent;
import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(OrderEvent event) {

        log.info("Processing payment synchronously | orderId={}", event.getOrderId());

        // 1️⃣ Idempotency check
        if (paymentRepository.existsByOrderId(event.getOrderId())) {
            log.warn("Payment already exists | orderId={}", event.getOrderId());
            return paymentRepository.findByOrderId(event.getOrderId());
        }

        // 2️⃣ Create payment
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getAmount());
        payment.setStatus("SUCCESS");

        Payment savedPayment = paymentRepository.save(payment);


        log.info("Payment processed | orderId={}", event.getOrderId());

        return savedPayment;
    }
}
