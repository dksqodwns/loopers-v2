package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Column(name = "ref_order_id", nullable = false)
    private Long orderId;

    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    public Payment(Long userId, Long orderId, Long totalPrice, PaymentType paymentType) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유저 id는 비어있을 수 없습니다.");
        }

        if (orderId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 id는 비어있을 수 없습니다.");
        }

        if (totalPrice < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 음수 일 수 없습니다.");
        }

        if (paymentType == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 수단은 비어있을 수 없습니다.");
        }

        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = PaymentStatus.REQUSETED;
        this.paymentType = paymentType;
        this.userId = userId;
    }

    public void complete() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void cancel() {
        this.status = PaymentStatus.FAILED;
    }
}
