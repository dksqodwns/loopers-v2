package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Point V1 API", description = "포인트 API")
public interface PointV1ApiSpec {
    @Operation(summary = "유저 포인트 조회")
    ApiResponse<PointDto.V1.PointResponse> getPoint(
            @Schema(name = "X-USER-ID")
            Long userId
    );

    @Operation(summary = "유저 포인트 충전")
    ApiResponse<PointDto.V1.PointResponse> charge(
            @Schema(name = "X-USER-ID")
            Long userId,

            @Schema(name = "충전 금액")
            PointDto.V1.ChargeRequest request
    );
}
