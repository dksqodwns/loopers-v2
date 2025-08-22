package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    @PostMapping("/orders/{orderId}/payments")
    public ApiResponse<PGPaymentDto.Response> requestPayment(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("orderId") Long orderId,
            @RequestBody PGPaymentDto.Request request
    ) {

        return ApiResponse.success();
    }

    @PostMapping("/examples/callback")
    public ApiResponse<Object> callback() {

        return ApiResponse.success();
    }
}
