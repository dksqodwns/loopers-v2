package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ORDER_ID = 100L;
    private static final Long TEST_TOTAL_PRICE = 5000L;
    private static final PaymentType TEST_PAYMENT_TYPE = PaymentType.POINT;

    @Nested
    @DisplayName("Payment 생성 시")
    class Create {

        @Test
        @DisplayName("userId가 null이면 CoreException(BAD_REQUEST)을 던진다")
        void throwCoreException_whenUserIdIsNull() {
            CoreException exception = assertThrows(CoreException.class, () ->
                    new Payment(null, TEST_ORDER_ID, TEST_TOTAL_PRICE, TEST_PAYMENT_TYPE)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("orderId가 null이면 CoreException(BAD_REQUEST)을 던진다")
        void throwCoreException_whenOrderIdIsNull() {
            CoreException exception = assertThrows(CoreException.class, () ->
                    new Payment(TEST_USER_ID, null, TEST_TOTAL_PRICE, TEST_PAYMENT_TYPE)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("totalPrice가 음수이면 CoreException(BAD_REQUEST)을 던진다")
        void throwCoreException_whenTotalPriceIsNegative() {
            CoreException exception = assertThrows(CoreException.class, () ->
                    new Payment(TEST_USER_ID, TEST_ORDER_ID, -100L, TEST_PAYMENT_TYPE)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("paymentType이 null이면 CoreException(BAD_REQUEST)을 던진다")
        void throwCoreException_whenPaymentTypeIsNull() {
            CoreException exception = assertThrows(CoreException.class, () ->
                    new Payment(TEST_USER_ID, TEST_ORDER_ID, TEST_TOTAL_PRICE, null)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("모든 값이 유효하면 Payment 객체가 올바르게 생성된다")
        void createPaymentSuccessfully_whenAllValuesAreValid() {
            Payment payment = new Payment(TEST_USER_ID, TEST_ORDER_ID, TEST_TOTAL_PRICE, TEST_PAYMENT_TYPE);

            assertAll(
                    () -> assertThat(payment.getUserId()).isEqualTo(TEST_USER_ID),
                    () -> assertThat(payment.getOrderId()).isEqualTo(TEST_ORDER_ID),
                    () -> assertThat(payment.getTotalPrice()).isEqualTo(TEST_TOTAL_PRICE),
                    () -> assertThat(payment.getPaymentType()).isEqualTo(TEST_PAYMENT_TYPE),
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REQUSETED)
            );
        }
    }

    @Nested
    @DisplayName("complete 메서드는")
    class Complete {

        @Test
        @DisplayName("호출 시 Payment 상태를 COMPLETED로 변경한다")
        void changeStatusToCompleted_whenCalled() {
            Payment payment = new Payment(TEST_USER_ID, TEST_ORDER_ID, TEST_TOTAL_PRICE, TEST_PAYMENT_TYPE);
            payment.complete();
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("cancel 메서드는")
    class Cancel {

        @Test
        @DisplayName("호출 시 Payment 상태를 FAILED로 변경한다")
        void changeStatusToFailed_whenCalled() {
            Payment payment = new Payment(TEST_USER_ID, TEST_ORDER_ID, TEST_TOTAL_PRICE, TEST_PAYMENT_TYPE);
            payment.cancel();
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }
    }
}
