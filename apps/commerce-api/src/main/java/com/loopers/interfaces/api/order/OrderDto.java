package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria.Order;
import com.loopers.application.order.OrderCriteria.Order.OrderItem;
import com.loopers.application.order.OrderResult;
import com.loopers.domain.order.OrderInfo;
import java.util.List;

public record OrderDto() {

    public record V1() {

        record OrderItemRequest(Long productId, Integer quantity) {
            private OrderItem toCriteria() {
                return new OrderItem(productId, quantity);
            }
        }

        public record OrderRequest(List<OrderItemRequest> items) {
            public Order toCriteira(final Long userId) {
                List<OrderItem> orderItems = this.items.stream()
                        .map(OrderItemRequest::toCriteria)
                        .toList();

                return new Order(userId, orderItems);
            }
        }

        public record OrderResponse(Long id, String status, Long totalPrice) {
            public static OrderResponse from(final OrderInfo orderInfo) {
                return new OrderResponse(
                        orderInfo.id(),
                        orderInfo.status().name(),
                        orderInfo.totalPrice());
            }
        }

        public record OrdersResponse(List<OrderResponse> orderResponses) {
            public static OrdersResponse from(final List<OrderInfo> orderInfos) {
                List<OrderResponse> orderResponses = orderInfos.stream()
                        .map(OrderResponse::from)
                        .toList();

                return new OrdersResponse(orderResponses);
            }
        }

        public record OrderItemDetailResponse(Long productId, String name, Integer quantity, Long price) {
        }

        public record OrderDetailResponse(Long id, String status, Long totalPrice, List<OrderItemDetailResponse> items) {
            public static OrderDetailResponse from(final OrderResult orderResult) {
                List<OrderItemDetailResponse> items = orderResult.items().stream()
                        .map(item -> new OrderItemDetailResponse(item.productId(), item.name(), item.quantity(), item.price()))
                        .toList();
                
                return new OrderDetailResponse(
                        orderResult.id(),
                        orderResult.status().name(),
                        orderResult.totalPrice(),
                        items
                );
            }
        }
    }
}
