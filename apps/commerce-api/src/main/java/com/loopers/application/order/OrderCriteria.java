package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OrderCriteria() {

    public record Order(Long userId, List<OrderItem> orderItems) {

        public record OrderItem(Long productId, Integer quantity) {
        }

        public OrderCommand.Order toCommand(final List<ProductInfo> productInfos) {
            final Map<Long, Integer> productQuantities = orderItems.stream()
                    .collect(Collectors.toMap(OrderItem::productId, OrderItem::quantity));

            final List<OrderCommand.Order.OrderItem> orderItems = productInfos.stream()
                    .map(productInfo -> new OrderCommand.Order.OrderItem(
                            productInfo.id(), productQuantities.get(productInfo.id()), productInfo.price()
                    ))
                    .toList();

            return new OrderCommand.Order(userId, orderItems);
        }

        private List<Long> getProductIds() {
            return orderItems.stream().map(OrderItem::productId).toList();
        }

        public ProductCommand.GetProducts toProductCommand() {
            return new ProductCommand.GetProducts(getProductIds());
        }
    }

    public record GetOrder(Long userId, Long productId) {
        public OrderCommand.GetOrder toCommand() {
            return new OrderCommand.GetOrder(userId, productId);
        }
    }
}
