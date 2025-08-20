package com.loopers.domain.order;

public record OrderItemInfo(Long productId, Integer quantity, Long price) {

    public static OrderItemInfo from(final OrderItem item) {
        return new OrderItemInfo(item.getProductId(), item.getQuantity(), item.getPrice());
    }
}
