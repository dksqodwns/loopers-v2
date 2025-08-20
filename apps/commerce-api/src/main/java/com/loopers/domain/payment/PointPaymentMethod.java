package com.loopers.domain.payment;

import com.loopers.domain.point.PointCommand.Use;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointPaymentMethod implements PaymentMethod {

    private final PointService pointService;

    @Override
    public void pay(Payment payment) {
        pointService.use(new Use(payment.getUserId(), payment.getTotalPrice()));
    }

    @Override
    public PaymentType getType() {
        return PaymentType.POINT;
    }
}
