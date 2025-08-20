package com.loopers.domain.order;

import java.util.List;

public record OrderInfo(
        Long id,
        Long userId,
        OrderStatus status,
        Long totalPrice,
        List<OrderItemInfo> orderItems
) {

    public static OrderInfo from(final Order order) {
        List<OrderItemInfo> orderItems = order.getItems().stream()
                .map(OrderItemInfo::from)
                .toList();

        return new OrderInfo(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.calculateTotalPrice(),
                orderItems
        );
    }


    public List<Long> getProductIds() {
        return orderItems.stream()
                .map(OrderItemInfo::productId)
                .toList();
    }


}
