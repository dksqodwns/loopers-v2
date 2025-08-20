package com.loopers.application.order;

import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.product.ProductInfo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OrderResult(
        Long id,
        Long userId,
        OrderStatus status,
        Long totalPrice,
        List<OrderItemResult> items
) {
    public record OrderItemResult(Long productId, String name, Integer quantity, Long price) {
    }

    public static OrderResult of(final OrderInfo orderInfo, final List<ProductInfo> productInfos) {
        final Map<Long, String> productNames = productInfos.stream()
                .collect(Collectors.toMap(ProductInfo::id, ProductInfo::name));

        final List<OrderItemResult> orderItems = orderInfo.orderItems().stream()
                .map(orderItem -> new OrderItemResult(
                        orderItem.productId(),
                        productNames.get(orderItem.productId()),
                        orderItem.quantity(),
                        orderItem.price()
                ))
                .toList();

        return new OrderResult(
                orderInfo.id(),
                orderInfo.userId(),
                orderInfo.status(),
                orderInfo.totalPrice(),
                orderItems
        );
    }


}
