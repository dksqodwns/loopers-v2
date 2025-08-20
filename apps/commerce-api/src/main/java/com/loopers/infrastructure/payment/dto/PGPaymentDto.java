package com.loopers.infrastructure.payment.dto;

import com.loopers.domain.payment.CardCompany;
import com.loopers.domain.payment.PaymentStatus;

public record PGPaymentDto() {

    public record Request(
            String orderId,
            CardCompany cardCompany,
            String cardNumber,
            Long amount,
            String callbackUrl
    ) {
        public static PGPaymentDto.Request from(
                String orderId,
                CardCompany cardCompany,
                String cardNumber,
                Long amount,
                String callbackUrl
        ) {
            return new PGPaymentDto.Request(
                    orderId,
                    cardCompany,
                    cardNumber,
                    amount,
                    callbackUrl
            );
        }
    }

    public record Response(
            String transactionKey,
            PaymentStatus status,
            String reason
    ) {
    }
}
