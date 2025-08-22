package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.loopers.infrastructure.http.PgFeignClient;
import com.loopers.infrastructure.http.PgPaymentDto;
import com.loopers.interfaces.api.payment.PGPaymentDto.CallBackRequest;
import com.loopers.support.error.CoreException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PgFeignClient pgFeignClient;

    @Nested
    @DisplayName("requestPayment 메서드는")
    class RequestPayment {

        @Test
        @DisplayName("정상적인 요청이면 PG사에 결제를 요청하고 transactionKey를 저장한다")
        void requestPaymentAndSaveTransactionKey_whenRequestIsValid() {
            // given
            PaymentCommand.Request command = new PaymentCommand.Request(1L, 1L, 5000L, CardCompany.SAMSUNG, "1234");
            String expectedTransactionKey = "test_tx_key";

            PgPaymentDto.Response pgResponse = new PgPaymentDto.Response(expectedTransactionKey, 1L, "SUCCESS", 5000L);
            when(pgFeignClient.requestPayment(any(), any(PgPaymentDto.Request.class))).thenReturn(pgResponse);

            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

            // when
            paymentService.requestPayment(command);

            // then
            verify(paymentRepository).save(paymentCaptor.capture());
            Payment savedPayment = paymentCaptor.getValue();

            assertAll(
                    () -> assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.REQUESTED),
                    () -> assertThat(savedPayment.getTransactionKey()).isEqualTo(expectedTransactionKey)
            );
        }
    }

    @Nested
    @DisplayName("processPaymentCallback 메서드는")
    class ProcessPaymentCallback {

        @Test
        @DisplayName("PG 콜백과 PG 직접 조회 상태가 다르면 예외를 던진다")
        void throwException_whenCallbackStatusMismatches() {
            // given
            CallBackRequest callback = new CallBackRequest("tx_key", "1", "SUCCESS");
            Payment existingPayment = Payment.of(1L, 1L, 5000L);

            when(paymentRepository.findByTransactionKey("tx_key")).thenReturn(Optional.of(existingPayment));

            PgPaymentDto.PaymentInfo pgInfo = new PgPaymentDto.PaymentInfo("tx_key", 1L, "FAILURE", 5000L);
            when(pgFeignClient.getPaymentInfo(any(), any())).thenReturn(pgInfo);

            // when & then
            assertThrows(CoreException.class, () -> paymentService.processPaymentCallback(callback));
        }

        @Test
        @DisplayName("정상적인 콜백이면 결제 상태를 COMPLETED로 변경한다")
        void changePaymentStatusToCompleted_whenCallbackIsSuccessful() {
            // given
            CallBackRequest callback = new CallBackRequest("tx_key", "1", "SUCCESS");
            Payment existingPayment = Payment.of(1L, 1L, 5000L);

            when(paymentRepository.findByTransactionKey("tx_key")).thenReturn(Optional.of(existingPayment));

            PgPaymentDto.PaymentInfo pgInfo = new PgPaymentDto.PaymentInfo("tx_key", 1L, "SUCCESS", 5000L);
            when(pgFeignClient.getPaymentInfo(any(), any())).thenReturn(pgInfo);

            // when
            paymentService.processPaymentCallback(callback);

            // then
            assertThat(existingPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }
    }
}
