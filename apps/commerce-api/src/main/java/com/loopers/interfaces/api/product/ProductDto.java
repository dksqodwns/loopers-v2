package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductResult;
import java.util.List;

public record ProductDto() {

    public record V1() {

        public record ProductResponse(
                Long id,
                String name,
                Long price,
                Integer stock,
                Long brnadId,
                String brnadName,
                Long likeCount
        ) {
            public static ProductResponse from(final ProductResult productResult) {
                return new ProductResponse(
                        productResult.id(),
                        productResult.name(),
                        productResult.price(),
                        productResult.stock(),
                        productResult.brnadId(),
                        productResult.brandName(),
                        productResult.likeCount()
                );
            }
        }

        public record SearchProductResponse(
                List<ProductResponse> products, Long totalElemnets, Integer totalPages
        ) {
            public static SearchProductResponse from(final ProductResult.SearchProducts searchProducts) {
                return new SearchProductResponse(
                        searchProducts.products().map(ProductResponse::from).stream().toList(),
                        searchProducts.products().getTotalElements(),
                        searchProducts.products().getTotalPages()
                );
            }
        }

    }
}
