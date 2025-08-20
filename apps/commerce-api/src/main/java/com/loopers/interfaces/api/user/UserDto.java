package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCriteria;
import com.loopers.application.user.UserResult;

public record UserDto() {

    public record V1() {

        public record RegisterRequest(
                String loginId,
                String email,
                String username,
                String birthDate,
                String gender
        ) {
            public UserCriteria.Register toCriteria() {
                return new UserCriteria.Register(
                        loginId,
                        email,
                        username,
                        birthDate,
                        gender
                );
            }
        }

        public record UserResponse(
                Long id,
                String loginId,
                String email,
                String username,
                String birthDate,
                String gender
        ) {

            public static UserResponse from(final UserResult userResult) {
                return new UserResponse(
                        userResult.id(),
                        userResult.loginId(),
                        userResult.email(),
                        userResult.username(),
                        userResult.birthDate(),
                        userResult.gender()
                );
            }
        }
    }
}
