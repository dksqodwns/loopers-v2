package com.loopers.infrastructure.scheduler;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentStatus;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Scheduled(cron = "0 */5 * * * *")
    public void syncPendingPayments() {
        ZonedDateTime cutoff = ZonedDateTime.now().minusMinutes(5);
        List<Payment> pendingPayments = paymentRepository.findAllByStatusAndCreatedAtBefore(PaymentStatus.REQUESTED,
                cutoff);

        if (pendingPayments.isEmpty()) {
            log.info("동기화 할 결제 건이 없습니다.");
            return;
        }

        log.info("총 {}건의 결제에 대해 상태 동기화를 시작합니다.", pendingPayments.size());
        for (Payment payment : pendingPayments) {
            try {
                paymentService.syncPaymentStatus(payment.getTransactionKey());
            } catch (Exception e) {
                log.error("결제 상태 동기화 중 오류 발생 transactionKey: {}, error: {}", payment.getTransactionKey(), e.getMessage());
            }
        }
    }
}
