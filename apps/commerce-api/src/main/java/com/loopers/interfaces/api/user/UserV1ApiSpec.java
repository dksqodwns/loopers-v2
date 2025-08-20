package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "유저 API")
public interface UserV1ApiSpec {

    @Operation(summary = "유저 회원가입 API")
    ApiResponse<UserDto.V1.UserResponse> regitser(
            @Schema(name = "회원가입 바디")
            UserDto.V1.RegisterRequest request
    );

    @Operation(summary = "유저 정보 조회 API")
    ApiResponse<UserDto.V1.UserResponse> getMyInfo(
            @Schema(name = "X-USER-ID")
            Long userId
    );
}
