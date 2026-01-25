package com.payment.consumer;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentEvent implements Serializable {

    private String orderId;
    private String paymentStatus;
    private LocalDateTime paymentTime;

    public PaymentEvent(String orderId, String paymentStatus) {
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.paymentTime = LocalDateTime.now();
    }

}
