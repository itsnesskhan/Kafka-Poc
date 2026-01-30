package com.order.service;

import com.order.dto.PaymentDTO;
import com.order.entity.Order;
import com.order.enums.ORDER_STATUS;
import com.order.producer.OrderEvent;
import com.order.repository.OrderRepository;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final RetryRegistry retryRegistry;

    @Value("${app.payment.service.url}")
    private String paymentServiceUrl;

    @PostConstruct
    public void registerRetryListener() {
        Retry retry = retryRegistry.retry("paymentServiceRetry");

        retry.getEventPublisher()
                .onRetry(event -> log.warn(
                        "Retry attempt #{} for paymentServiceRetry | lastException={}",
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable().toString()
                ))
                .onSuccess(event -> log.info(
                        "Payment succeeded after {} retries",
                        event.getNumberOfRetryAttempts()
                ))
                .onError(event -> log.error(
                        "Payment failed after {} retries",
                        event.getNumberOfRetryAttempts()
                ));
    }

    @io.github.resilience4j.retry.annotation.Retry(name = "paymentServiceRetry", fallbackMethod = "paymentFallback")
    public Order createOrder(Order order) {

        // 1️⃣ Save order
        order.setStatus(ORDER_STATUS.CREATED.getValue());
        Order savedOrder = orderRepository.save(order);

        // 2️⃣ Prepare OrderEvent
        OrderEvent event = new OrderEvent(order);

        // 3️⃣ Call Payment Service synchronously
        ResponseEntity<PaymentDTO> response =
                restTemplate.postForEntity(
                        paymentServiceUrl + "/api/payments/process",
                        event,
                        PaymentDTO.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Payment failed for orderId="
                    + savedOrder.getOrderId());
        }

        log.info("Order & Payment completed | orderId={}", savedOrder.getOrderId());

        return savedOrder;
    }

    public Order paymentFallback(Order order, Throwable ex) {

        log.error("Payment service failed after retries. Reason: {}", ex.getMessage());
        order.setStatus(ORDER_STATUS.PAYMENT_PENDING.getValue());
        orderRepository.save(order);

        return order;
    }
}
