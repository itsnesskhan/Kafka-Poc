package com.payment.consumer;

import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final PaymentRepository paymentRepository;

    @Value("${app.kafka.topic.payment-completed}")
    private String paymentCompletedTopic;

    @KafkaListener(topics = "${app.kafka.topic.order-created}")
    public void consume(OrderEvent event) {

        log.info("PaymentConsumer received OrderEvent: {}", event);

        // Idempotency check
        if (paymentRepository.existsByOrderId(event.getOrderId())) {
            log.warn("Payment already exists for orderId={}", event.getOrderId());
            return;
        }

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getAmount());
        payment.setStatus("SUCCESS");

        paymentRepository.save(payment);

        // Publish payment completed event
        kafkaTemplate.send(
                paymentCompletedTopic,
                event.getOrderId(),
                new PaymentEvent(event.getOrderId(), "SUCCESS")
        );

        log.info("PaymentEvent published | orderId={} status=SUCCESS", event.getOrderId());
    }
}
