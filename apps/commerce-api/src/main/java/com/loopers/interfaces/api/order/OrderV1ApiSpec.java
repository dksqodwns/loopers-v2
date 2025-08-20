package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order API", description = "Order API")
public interface OrderV1ApiSpec {

    @Operation(summary = "상품 주문")
    ApiResponse<Object> order(
            @Schema(name = "X-USER-ID")
            Long userId,

            @Schema(name = "DTO")
            OrderDto.V1.OrderRequest request
    );

    @Operation(summary = "주문 목록 조회")
    ApiResponse<OrderDto.V1.OrdersResponse> getOrders(
            @Schema(name = "X-USER-ID")
            Long userId
    );


    @Operation(summary = "주문 상세 조회")
    ApiResponse<OrderDto.V1.OrderDetailResponse> getOrder(
            @Schema(name = "X-USER-ID")
            Long userId,

            @Schema(name = "주문 ID")
            Long orderId
    );
}
