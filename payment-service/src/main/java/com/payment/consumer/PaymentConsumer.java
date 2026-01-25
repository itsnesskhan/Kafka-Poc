package com.payment.consumer;

import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @KafkaListener(topics = "order-created")
    public void consume(OrderEvent event) {
        log.info("payment consumer received data {}",event);
        // idempotency check
        if (paymentRepository.existsByOrderId(event.getOrderId())) {
            return;
        }

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getAmount());
        payment.setStatus("SUCCESS");

        paymentRepository.save(payment);

        // publish payment completed event
        kafkaTemplate.send(
                "payment-completed",
                event.getOrderId(),
                new PaymentEvent(event.getOrderId(), "SUCCESS")
        );
    }
}
