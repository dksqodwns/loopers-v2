package com.loopers.interfaces.api.payment;

import com.loopers.domain.payment.PaymentService;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/callback")
    public ApiResponse<Object> callback(@RequestBody PGPaymentDto.CallBackRequest request) {
        paymentService.processPaymentCallback(request);
        return ApiResponse.success();
    }
}
