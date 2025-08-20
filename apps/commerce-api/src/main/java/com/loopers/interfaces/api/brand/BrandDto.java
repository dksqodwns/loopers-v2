package com.loopers.interfaces.api.brand;

import com.loopers.domain.brand.BrandInfo;

public record BrandDto() {

    public record V1() {
        public record BrandResponse(Long id, String name) {
            public static BrandResponse from(BrandInfo brandInfo) {
                return new BrandResponse(brandInfo.id(), brandInfo.name());
            }
        }
    }

}
