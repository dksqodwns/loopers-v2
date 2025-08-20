package com.loopers.domain.product;

public record ProductInfo(
        Long id,
        BrandId brandId,
        String name,
        Long price
) {

    public static ProductInfo from(final Product product) {
        return new ProductInfo(
                product.getId(),
                product.getBrandId(),
                product.getName(),
                product.getPrice()
        );
    }

    public Long getBrandId() {
        return brandId.getBrandId();
    }
}
