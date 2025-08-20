package com.loopers.application.port.out;


import com.loopers.domain.order.Order;
import com.loopers.domain.payment.CardInfo;

public interface PaymentGatewayPort {
    void requestPayment(Order order, CardInfo cardInfo);
}
