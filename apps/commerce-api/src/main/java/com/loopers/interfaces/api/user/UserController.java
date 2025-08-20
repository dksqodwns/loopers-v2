package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserResult;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserDto.V1.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserV1ApiSpec {
    private final UserFacade userFacade;

    @Override
    @PostMapping("")
    public ApiResponse<UserDto.V1.UserResponse> regitser(@RequestBody() final UserDto.V1.RegisterRequest request) {
        UserResult userResult = userFacade.register(request.toCriteria());
        UserResponse response = UserResponse.from(userResult);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<UserDto.V1.UserResponse> getMyInfo(@RequestHeader("X-USER-ID") final Long userId) {
        UserResult userResult = userFacade.getUser(userId);
        UserResponse response = UserResponse.from(userResult);
        return ApiResponse.success(response);
    }
}
