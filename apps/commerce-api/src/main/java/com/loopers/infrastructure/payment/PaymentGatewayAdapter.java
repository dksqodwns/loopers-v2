package com.loopers.infrastructure.payment;

import com.loopers.application.port.out.PaymentGatewayPort;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.CardInfo;
import com.loopers.domain.payment.CardPayment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.infrastructure.payment.client.PaymentGatewayFeignClient;
import com.loopers.infrastructure.payment.dto.PGPaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayAdapter implements PaymentGatewayPort {

    private final PaymentGatewayFeignClient paymentGatewayFeignClient;
    private final PaymentRepository paymentRepository;

    @Value("${server.callback-url}")
    private String callbackUrl;

    @Override
    public void requestPayment(Order order, CardInfo cardInfo) {
        PGPaymentDto.Request request = PGPaymentDto.Request.from(
                String.valueOf(order.getId()),
                cardInfo.cardCompany(),
                cardInfo.cardNumber(),
                order.calculateTotalPrice(),
                callbackUrl
        );

        PGPaymentDto.Response response = paymentGatewayFeignClient.requestPayment(
                order.getUserId(), request
        );

        CardPayment cardPayment = CardPayment.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .totalPrice(order.calculateTotalPrice())
                .paymentGateway(PaymentGateway.LOOPERS)
                .transactionKey(response.transactionKey())
                .cardCompany(cardInfo.cardCompany())
                .cardNumber(cardInfo.cardNumber())
                .build();

        paymentRepository.save(cardPayment);
    }
}
