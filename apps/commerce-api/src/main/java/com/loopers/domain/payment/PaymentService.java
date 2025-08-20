package com.loopers.domain.payment;

import com.loopers.application.port.out.PaymentGatewayPort;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.OrderStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentGatewayPort paymentGatewayPort;

    public void requestPayment(Long orderId, CardInfo cardInfo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 주문을 찾을 수 없습니다. orderId: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 결제가 진행중이거나 완료 된 주문 입니다.");
        }
        order.confirm();

        paymentGatewayPort.requestPayment(order, cardInfo);
    }
}
