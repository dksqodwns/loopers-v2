package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderInfo order(final OrderCommand.Order command) {
        List<OrderItem> orderItems = command.orderItems().stream()
                .map(orderItem -> new OrderItem(orderItem.productId(), orderItem.price(), orderItem.quantity()))
                .toList();
        final Order order = new Order(command.userId(), orderItems);
        final Order savedOrder = orderRepository.save(order);
        return OrderInfo.from(savedOrder);
    }

    public OrderInfo getOrder(final OrderCommand.GetOrder command) {
        return orderRepository.findByIdAndUserId(command.orderId(), command.userId())
                .map(OrderInfo::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                        "해당하는 주문을 찾을 수 없습니다. userId: " + command.userId() + ", orderId: " + command.orderId()));
    }

    @Transactional(readOnly = true)
    public List<OrderInfo> getOrders(final OrderCommand.GetOrders command) {
        return orderRepository.findAllByUserId(command.userId()).stream()
                .map(OrderInfo::from)
                .toList();
    }

    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다. orderId: " + orderId));
        order.confirm();
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다. orderId: " + orderId));
        order.complete();
    }
}
