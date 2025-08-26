package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ORDER_ID = 100L;
    private static final Long TEST_AMOUNT = 5000L;

    @Nested
    @DisplayName("Payment 생성 시")
    class Create {

        @Test
        @DisplayName("of 팩토리 메서드를 사용하면 Payment 객체가 올바르게 생성된다")
        void createPaymentSuccessfully_whenUsingFactoryMethod() {
            Payment payment = Payment.of(TEST_ORDER_ID, TEST_USER_ID, TEST_AMOUNT);

            assertAll(
                    () -> assertThat(payment.getUserId()).isEqualTo(TEST_USER_ID),
                    () -> assertThat(payment.getOrderId()).isEqualTo(TEST_ORDER_ID),
                    () -> assertThat(payment.getAmount()).isEqualTo(TEST_AMOUNT),
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REQUESTED)
            );
        }
    }

    @Nested
    @DisplayName("상태 변경 메서드는")
    class ChangeStatus {

        @Test
        @DisplayName("complete 호출 시 Payment 상태를 COMPLETED로 변경한다")
        void changeStatusToCompleted_whenCompleteIsCalled() {
            Payment payment = Payment.of(TEST_ORDER_ID, TEST_USER_ID, TEST_AMOUNT);
            payment.complete();
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }

        @Test
        @DisplayName("fail 호출 시 Payment 상태를 FAILED로 변경한다")
        void changeStatusToFailed_whenFailIsCalled() {
            Payment payment = Payment.of(TEST_ORDER_ID, TEST_USER_ID, TEST_AMOUNT);
            payment.fail();
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("assignTransactionKey 메서드는")
    class AssignTransactionKey {

        @Test
        @DisplayName("호출 시 transactionKey를 할당한다")
        void assignTransactionKey_whenCalled() {
            Payment payment = Payment.of(TEST_ORDER_ID, TEST_USER_ID, TEST_AMOUNT);
            String transactionKey = "test_transaction_key";
            payment.assignTransactionKey(transactionKey);
            assertThat(payment.getTransactionKey()).isEqualTo(transactionKey);
        }
    }
}