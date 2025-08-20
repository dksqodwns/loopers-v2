package com.loopers.application.like;

import com.loopers.domain.product.ProductInfo;
import java.util.List;

public record LikeResult() {

    public record GetLikeProducts(List<GetLikeProduct> products) {

        public static GetLikeProducts from(final List<ProductInfo> productInfos) {
            final List<GetLikeProduct> products = productInfos.stream()
                    .map(GetLikeProduct::from)
                    .toList();

            return new GetLikeProducts(products);
        }
    }

    public record GetLikeProduct(Long id, String name, Long price) {
        public static GetLikeProduct from(final ProductInfo productInfo) {
            return new GetLikeProduct(
                    productInfo.id(),
                    productInfo.name(),
                    productInfo.price()
            );
        }
    }

}
