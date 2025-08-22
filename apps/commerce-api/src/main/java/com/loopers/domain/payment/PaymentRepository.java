package com.loopers.domain.payment;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByTransactionKey(String transactionKey);

    List<Payment> findAllByStatusAndCreatedAtBefore(PaymentStatus status, ZonedDateTime createdAt);

}
