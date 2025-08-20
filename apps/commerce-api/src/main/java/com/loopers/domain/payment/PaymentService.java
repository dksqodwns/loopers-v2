package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Map<PaymentType, PaymentMethod> paymentMethods;

    public PaymentService(PaymentRepository paymentRepository, List<PaymentMethod> paymentMethods) {
        this.paymentRepository = paymentRepository;
        this.paymentMethods = paymentMethods.stream()
                .collect(Collectors.toUnmodifiableMap(PaymentMethod::getType, Function.identity()));
    }

    @Transactional
    public Payment request(Long orderId, Long userId, Long price, PaymentType paymentType) {
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 결제가 요청 된 주문 입니다. orderId: " + orderId);
        }
        Payment payment = new Payment(userId, orderId, price, paymentType);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void pay(Payment payment) {
        PaymentMethod paymentMethod = paymentMethods.get(payment.getPaymentType());
        paymentMethod.pay(payment);
        payment.complete();
    }


}
