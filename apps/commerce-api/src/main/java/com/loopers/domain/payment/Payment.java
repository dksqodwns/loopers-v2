package com.loopers.domain.payment;


import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "ref_order_id", nullable = false)
    private Long orderId;

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Column(unique = true)
    private String transactionKey;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Builder
    public Payment(Long orderId, Long userId, Long amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = PaymentStatus.REQUESTED;
    }

    public static Payment of(Long orderId, Long userId, Long amount) {
        return new Payment(orderId, userId, amount);
    }

    public void assignTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    public void complete() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }


}
