package com.loopers.infrastructure.payment;

import com.loopers.application.port.out.PaymentGatewayPort;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.CardPayment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayAdapter implements PaymentGatewayPort {

    private final PaymentRepository paymentRepository;

    @Override
    public void requestPayment(Order order) {
        CardPayment cardPayment = CardPayment.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .totalPrice(order.calculateTotalPrice())
                .paymentGateway(PaymentGateway.LOOPERS)
                .transactionKey("transactionKey_" + order.getId())
                .build();

        paymentRepository.save(cardPayment);
    }
}
