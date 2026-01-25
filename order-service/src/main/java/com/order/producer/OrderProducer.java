package com.order.producer;

import com.order.entity.Order;
import com.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final OrderRepository orderRepository;

    public void createOrder(Order order) {

        order.setStatus("CREATED");
        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent(order);

        kafkaTemplate.send("order-created", savedOrder.getOrderId(), event);

        log.info("OrderEvent published | orderId={} amount={}",
                savedOrder.getOrderId(), savedOrder.getAmount());
    }
}
