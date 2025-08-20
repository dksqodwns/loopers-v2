package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

@Tag(name = "Product V1 API", description = "상품 API")
public interface ProductV1ApiSpec {
    @Operation(
            summary = "상품 정보 조회"
    )
    ApiResponse<ProductDto.V1.ProductResponse> getProduct(
            @Schema(name = "상품 ID")
            Long productId
    );

    @Operation(
            summary = "상품 목록 검색 및 조회"
    )
    ApiResponse<ProductDto.V1.SearchProductResponse> searchProducts(
            @Parameter(description = "브랜드 ID (선택 사항)")
            Long brandId,
            @Parameter(description = "정렬 및 페이징 정보 (예: ?page=0&size=20&sort=price,desc)")
            Pageable pageable
    );
}
