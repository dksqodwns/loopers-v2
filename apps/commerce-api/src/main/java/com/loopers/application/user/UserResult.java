package com.loopers.application.user;

import com.loopers.domain.user.UserInfo;

public record UserResult(
        Long id, String loginId, String email, String username, String birthDate, String gender
) {

    public static UserResult from(final UserInfo userInfo) {
        return new UserResult(
                userInfo.id(),
                userInfo.loginId().getLoginId(),
                userInfo.email().getEmail(),
                userInfo.username(),
                userInfo.birthDate().toString(),
                userInfo.gender().name()
        );
    }
}
