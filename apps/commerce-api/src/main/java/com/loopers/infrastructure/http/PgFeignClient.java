package com.loopers.infrastructure.http;

import com.loopers.infrastructure.http.PgPaymentDto.PaymentInfo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "payment-gateway",
        url = "${pg.gateway.url}"
)
public interface PgFeignClient {

    @PostMapping("/api/v1/payments")
    PgPaymentDto.Response requestPayment(@RequestHeader("X-USER-ID") Long userId, @RequestBody PgPaymentDto.Request request);

    @GetMapping("/api/v1/payments/{transactionKey}")
    PgPaymentDto.PaymentInfo getPaymentInfo(@RequestHeader("X-USER-ID") Long userId,
                                            @PathVariable("transactionKey") String transactionKey);

    @GetMapping("/api/v1/payments")
    List<PgPaymentDto.PaymentInfo> getPaymentsInfos(@RequestHeader("X-USER-ID") Long userId, @RequestParam Long orderId);
}
