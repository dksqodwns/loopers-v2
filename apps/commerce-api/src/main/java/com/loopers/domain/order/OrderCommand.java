package com.loopers.domain.order;

import java.util.List;

public record OrderCommand() {

    public record Order(Long userId, List<OrderItem> orderItems) {
        public record OrderItem(Long productId, Integer quantity, Long price) {
        }
    }

    public record GetOrders(Long userId) {
    }

    public record GetOrder(Long userId, Long orderId) {
    }

}
