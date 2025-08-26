package com.loopers.infrastructure.http;

public record PgPaymentDto() {

    public record Request(
            String orderId,
            Long amount,
            String callbackUrl,
            String cardType,
            String cardNo
    ) {
    }


    public record Response(
            String transactionKey,
            Long orderId,
            String status,
            Long amount
    ) {


    }

    public record PaymentInfo(
            String transactionKey,
            Long orderId,
            String status,
            Long amount
    ) {

    }

}
