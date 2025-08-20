package com.loopers.application.port.out;


import com.loopers.domain.order.Order;

public interface PaymentGatewayPort {
    void requestPayment(Order order);
}
