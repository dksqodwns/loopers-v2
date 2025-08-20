package com.loopers.application.order.event;


import com.loopers.application.port.out.PaymentGatewayPort;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.event.OrderPlacedEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPlacedEventHandler {

    private final OrderRepository orderRepository;
    private final PaymentGatewayPort paymentGatewayPort;

    public void handle(OrderPlacedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));

        paymentGatewayPort.requestPayment(order);
    }
}
