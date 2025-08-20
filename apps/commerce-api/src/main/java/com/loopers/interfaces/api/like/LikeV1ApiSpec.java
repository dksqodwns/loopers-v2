package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "좋아요 API", description = "좋아요 API")
public interface LikeV1ApiSpec {

    @Operation(summary = "좋아요 등록")
    ApiResponse<Object> like(
            @Schema(name = "X-USER-ID")
            Long userId,

            @Schema(name = "상품 Id")
            Long productId
    );


    @Operation(summary = "좋아요 취소")
    ApiResponse<Object> unlike(
            @Schema(name = "X-USER-ID")
            Long userId,

            @Schema(name = "상품 Id")
            Long productId
    );


    @Operation(summary = "좋아요 상품 조회")
    ApiResponse<LikeDto.V1.GetLikeProductsResponse> getLikeProducts(
            @Schema(name = "X-USER-ID")
            Long userId
    );
}
