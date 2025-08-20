package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.stock.ProductStockInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public record ProductResult(
        Long id,
        String name,
        Long price,
        Integer stock,
        Long likeCount,
        Long brnadId,
        String brandName
) {
    public static ProductResult of(
            final ProductInfo productInfo,
            final BrandInfo brandInfo,
            final Optional<ProductStockInfo> productStockInfo,
            final Optional<ProductCountInfo> productCountInfo
    ) {
        return new ProductResult(
                productInfo.id(),
                productInfo.name(),
                productInfo.price(),
                productStockInfo.map(ProductStockInfo::getStock).orElse(null),
                productCountInfo.map(ProductCountInfo::getLikeCount).orElse(null),
                brandInfo.id(),
                brandInfo.name()
        );
    }

    public record SearchProducts(Page<ProductResult> products) {
        public static SearchProducts of(
                final Page<ProductInfo> productInfos,
                final List<BrandInfo> brandInfos,
                final List<ProductStockInfo> productStockInfos,
                final List<ProductCountInfo> productCountInfos
        ) {
            final Map<Long, BrandInfo> brands = brandInfos.stream()
                    .collect(Collectors.toMap(BrandInfo::id, brandInfo -> brandInfo));

            final Map<Long, ProductStockInfo> stocks = productStockInfos.stream()
                    .collect(Collectors.toMap(ProductStockInfo::productId, productStockInfo -> productStockInfo));

            final Map<Long, ProductCountInfo> counts = productCountInfos.stream()
                    .collect(Collectors.toMap(ProductCountInfo::productId, productCountInfo -> productCountInfo));

            final Page<ProductResult> productResults = productInfos.map(
                    productInfo -> {
                        final Optional<ProductStockInfo> stockInfo = Optional.ofNullable(stocks.get(productInfo.id()));
                        final Optional<ProductCountInfo> countInfo = Optional.ofNullable(counts.get(productInfo.id()));
                        final BrandInfo brandInfo = brands.get(productInfo.getBrandId());

                        return ProductResult.of(productInfo, brandInfo, stockInfo, countInfo);
                    }
            );

            return new SearchProducts(productResults);
        }
    }
}
