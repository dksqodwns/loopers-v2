package com.loopers.domain.payment;

public class PaymentCommand {
    public record Request(Long orderId, Long userId, Long amount, CardCompany cardType, String cardNo) {

    }
}
