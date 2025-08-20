package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PointService pointService;

    @BeforeEach
    void setUp() {
        PointPaymentMethod pointPaymentMethod = new PointPaymentMethod(pointService);
        List<PaymentMethod> paymentMethodList = List.of(pointPaymentMethod);
        paymentService = new PaymentService(paymentRepository, paymentMethodList);
    }

    @Nested
    @DisplayName("request 메서드는")
    class Request {

        @Test
        @DisplayName("이미 결제가 요청된 주문이면 CoreException(CONFLICT)을 던진다")
        void throwCoreException_whenPaymentAlreadyRequested() {
            // Given
            Long orderId = 1L;
            Long userId = 1L;
            Long price = 1000L;
            PaymentType paymentType = PaymentType.POINT;

            given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(mock(Payment.class)));

            // When & Then
            CoreException exception = assertThrows(CoreException.class, () ->
                    paymentService.request(orderId, userId, price, paymentType)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }

        @Test
        @DisplayName("정상적인 요청이면 Payment를 저장하고 반환한다")
        void saveAndReturnPayment_whenRequestIsValid() {
            // Given
            Long orderId = 1L;
            Long userId = 1L;
            Long price = 1000L;
            PaymentType paymentType = PaymentType.POINT;
            Payment expectedPayment = new Payment(userId, orderId, price, paymentType);

            given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());
            given(paymentRepository.save(any(Payment.class))).willReturn(expectedPayment);

            // When
            Payment result = paymentService.request(orderId, userId, price, paymentType);

            // Then
            assertThat(result).isEqualTo(expectedPayment);
            verify(paymentRepository, times(1)).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("pay 메서드는")
    class Pay {

        @Test
        @DisplayName("결제 성공 시 PaymentMethod의 pay를 호출하고 Payment를 완료 상태로 변경한다")
        void callPaymentMethodPayAndCompletePayment_whenPaymentSuccessful() {
            // Given
            Long userId = 1L;
            Long orderId = 1L;
            Long price = 1000L;
            PaymentType paymentType = PaymentType.POINT;
            Payment payment = new Payment(userId, orderId, price, paymentType);

            given(pointService.use(any(PointCommand.Use.class))).willReturn(null);

            // When
            paymentService.pay(payment);

            // Then
            verify(pointService, times(1)).use(any());
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }

    }
}
