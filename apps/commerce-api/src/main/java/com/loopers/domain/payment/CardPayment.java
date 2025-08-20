package com.loopers.domain.payment;

import com.loopers.domain.converter.CryptoConverter;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "card_payments")
@DiscriminatorValue("CARD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardPayment extends Payment {

    @Column(unique = true, nullable = false)
    private String transactionKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGateway paymentGateway;

    @Enumerated(EnumType.STRING)
    private CardCompany cardCompany;

    @Convert(converter = CryptoConverter.class)
    private String cardNumber;

    @Builder
    public CardPayment(
            Long userId,
            Long orderId,
            Long totalPrice,
            String transactionKey,
            PaymentGateway paymentGateway,
            CardCompany cardCompany,
            String cardNumber
    ) {
        super(userId, orderId, totalPrice);

        if (transactionKey == null || transactionKey.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "거래 키는 비어있을 수 없습니다.");
        }
        if (paymentGateway == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "PG사 정보는 비어있을 수 없습니다.");
        }
        this.transactionKey = transactionKey;
        this.paymentGateway = paymentGateway;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
    }
}
