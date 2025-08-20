package com.loopers.infrastructure.payment.client;

import com.loopers.infrastructure.payment.dto.PGPaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "payment-gateway",
        url = "${pg.gateway.url}"
)
public interface PaymentGatewayFeignClient {
    PGPaymentDto.Response requestPayment(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody PGPaymentDto.Request request
    );
}
