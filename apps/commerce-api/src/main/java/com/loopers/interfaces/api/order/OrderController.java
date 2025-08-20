package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria.GetOrder;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderResult;
import com.loopers.domain.order.OrderCommand.GetOrders;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.infrastructure.count.ProductCountRepositoryImpl;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.OrderDto.V1.OrderDetailResponse;
import com.loopers.interfaces.api.order.OrderDto.V1.OrdersResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderV1ApiSpec {
    private final OrderFacade orderFacade;
    private final OrderService orderService;
    private final ProductCountRepositoryImpl productCountRepositoryImpl;

    @Override
    @PostMapping
    public ApiResponse<Object> order(
            @RequestHeader("X-USER-ID") final Long userId,
            @RequestBody final OrderDto.V1.OrderRequest request) {
        orderFacade.order(request.toCriteira(userId));
        return ApiResponse.success();
    }

    @Override
    @GetMapping
    public ApiResponse<OrdersResponse> getOrders(
            @RequestHeader("X-USER-ID") final Long userId
    ) {
        final List<OrderInfo> orderInfos = orderService.getOrders(new GetOrders(userId));
        return ApiResponse.success(OrderDto.V1.OrdersResponse.from(orderInfos));
    }

    @Override
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrder(
            @RequestHeader("X-USER-ID") final Long userId,
            @PathVariable final Long orderId
    ) {
        final OrderResult orderResult = orderFacade.getOrder(new GetOrder(userId, orderId));
        return ApiResponse.success(OrderDetailResponse.from(orderResult));
    }
}
