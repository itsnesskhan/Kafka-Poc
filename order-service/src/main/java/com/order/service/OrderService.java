package com.order.service;

import com.order.dto.PaymentDTO;
import com.order.entity.Order;
import com.order.producer.OrderEvent;
import com.order.repository.OrderRepository;
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

    @Value("${app.payment.service.url}")
    private String paymentServiceUrl;

    public Order createOrder(Order order) {

        // 1️⃣ Save order
        order.setStatus("CREATED");
        Order savedOrder = orderRepository.save(order);
        log.info("order created with id : {}",savedOrder.getOrderId());

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
}
