package com.loopers.application.order;

import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.event.OrderPlacedEvent;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductCommand.GetProducts;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.ProductStockCommand;
import com.loopers.domain.stock.ProductStockService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final PointService pointService;
    private final ProductStockService productStockService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void order(final OrderCriteria.Order criteria) {
        final List<ProductInfo> productInfos = productService.getProducts(criteria.toProductCommand());

        final OrderInfo orderInfo = orderService.order(criteria.toCommand(productInfos));

        orderInfo.orderItems().forEach(orderItem ->
                productStockService.decrease(new ProductStockCommand.Decrease(orderItem.productId(), orderItem.quantity()))
        );

        eventPublisher.publishEvent(new OrderPlacedEvent(orderInfo.id(), orderInfo.userId(), orderInfo.totalPrice()));
    }

    @Transactional(readOnly = true)
    public OrderResult getOrder(final OrderCriteria.GetOrder criteria) {
        OrderInfo orderInfo = orderService.getOrder(criteria.toCommand());
        List<ProductInfo> productInfos = productService.getProducts(new GetProducts(orderInfo.getProductIds()));

        return OrderResult.of(orderInfo, productInfos);
    }
}
