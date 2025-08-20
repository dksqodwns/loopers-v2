package com.loopers.interfaces.api.brand;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "브랜드 조회 API", description = "브랜드 조회 API")
public interface BrandV1ApiSpec {

    @Operation(summary = "브랜도 상세 조회")
    ApiResponse<BrandDto.V1.BrandResponse> getBrand(Long brandId);
}
