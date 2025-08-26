package com.loopers.domain.payment;

import com.loopers.infrastructure.http.PgFeignClient;
import com.loopers.infrastructure.http.PgPaymentDto;
import com.loopers.infrastructure.http.PgPaymentDto.PaymentInfo;
import com.loopers.infrastructure.http.PgPaymentDto.Request;
import com.loopers.interfaces.api.payment.PGPaymentDto.CallBackRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PgFeignClient pgFeignClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CircuitBreaker(name = "payment-gateway", fallbackMethod = "requestPaymentFallback")
    @Retry(name = "payment-gateway")
    public void requestPayment(PaymentCommand.Request command) {
        Payment payment = Payment.of(command.orderId(), command.userId(), command.amount());
        paymentRepository.save(payment);

        PgPaymentDto.Request request = new Request(
                String.valueOf(command.orderId()),
                command.amount(),
                "http://localhost:8080/api/v1/payments/callback",
                command.cardType().toString(),
                command.cardNo()
        );

        PgPaymentDto.Response response = pgFeignClient.requestPayment(command.userId(), request);

        payment.assignTransactionKey(response.transactionKey());
    }

    public void requestPaymentFallback(PaymentCommand.Request command, Throwable throwable) {
        log.error("PG 결제 요청 실패 및 폴백 실행. orderId: {}, error: {}", command.orderId(), throwable.getMessage());
        paymentRepository.findByOrderId(command.orderId()).ifPresent(Payment::fail);
    }

    public void syncPaymentStatus(String transactionKey) {
        Payment payment = paymentRepository.findByTransactionKey(transactionKey)
                .orElseThrow(
                        () -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보를 찾을 수 없습니다. transactionKey: " + transactionKey)
                );

        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.FAILED) {
            log.info("이미 처리 된 결제 입니다. transactionKey: {}", transactionKey);
            return;
        }

        PaymentInfo paymentInfo = pgFeignClient.getPaymentInfo(payment.getUserId(), payment.getTransactionKey());
        PaymentStatus paymentStatus = PaymentStatus.from(paymentInfo.status());

        if (paymentStatus == PaymentStatus.COMPLETED) {
            payment.complete();
        } else {
            payment.fail();
        }
    }

    @Transactional
    public void processPaymentCallback(CallBackRequest request) {
        log.info("PG 콜백 수신: {}", request);
        Payment payment = paymentRepository.findByTransactionKey(request.transactionKey())
                .orElseThrow(
                        () -> {
                            log.error("해당하는 결제 정보를 찾을 수 없습니다. transactionKey: {}", request.transactionKey());
                            return new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보를 찾을 수 없습니다. transactionKey: " +
                                    request.transactionKey());
                        }
                );

        PaymentInfo paymentInfo = pgFeignClient.getPaymentInfo(payment.getUserId(), payment.getTransactionKey());
        if (!paymentInfo.status().equals(request.status())) {
            log.error("콜백과 PG 조회의 상태가 불일치 합니다. 콜백: {}, PG: {}", request.status(), paymentInfo.status());

            throw new CoreException(ErrorType.BAD_REQUEST,
                    "콜백과 PG 조회의 상태가 불일치 합니다. 콜백: " + request.status() + ", PG: " + paymentInfo.status());
        }
        PaymentStatus paymentStatus = PaymentStatus.from(request.status());

        if (paymentStatus == PaymentStatus.COMPLETED) {
            payment.complete();
        } else {
            payment.fail();
        }
    }
}
