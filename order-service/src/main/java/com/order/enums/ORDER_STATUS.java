package com.order.enums;

import lombok.Getter;

@Getter
public enum ORDER_STATUS {

    CREATED("CREATED"),
    PAYMENT_PENDING("PAYMENT_PENDING");

    private final String value;

    ORDER_STATUS(String value) {
        this.value = value;
    }

}

