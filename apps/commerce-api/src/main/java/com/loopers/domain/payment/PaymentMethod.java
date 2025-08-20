package com.loopers.domain.payment;

public interface PaymentMethod {
    PaymentType getType();

    void pay(Payment payment);
}
