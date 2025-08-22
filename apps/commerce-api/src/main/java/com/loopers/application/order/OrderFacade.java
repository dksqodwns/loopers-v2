package com.loopers.application.order;

import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.event.OrderPlacedEvent;
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
    private final ProductStockService productStockService;

    @Transactional
    public void order(final OrderCriteria.Order criteria) {
        final List<ProductInfo> productInfos = productService.getProducts(criteria.toProductCommand());

        final OrderInfo orderInfo = orderService.order(criteria.toCommand(productInfos));

        orderInfo.orderItems().forEach(orderItem ->
                productStockService.decrease(new ProductStockCommand.Decrease(orderItem.productId(), orderItem.quantity()))
        );

        /*
        * TODO: 주문 생성 이후 해야하는 것
        *  1. 결제 요청: 결제에 대한 정보를 PG사에 찔러야 함 (FeignClient로 결제 요청 보냄)
        *  2. 결제 콜백에 따른 Resilience 처리 (callback으로 받은 응답에 대한 처리)
        *  3. 스케줄러 돌아서, 결제 처리 업데이트 안된건 확인 후 반영 (transactionKey로 확인 가능)
        *  4. 결제가 안된 건은 실패 처리
        * */
    }

    @Transactional(readOnly = true)
    public OrderResult getOrder(final OrderCriteria.GetOrder criteria) {
        OrderInfo orderInfo = orderService.getOrder(criteria.toCommand());
        List<ProductInfo> productInfos = productService.getProducts(new GetProducts(orderInfo.getProductIds()));

        return OrderResult.of(orderInfo, productInfos);
    }
}
