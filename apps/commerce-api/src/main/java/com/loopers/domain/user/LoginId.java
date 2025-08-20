package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginId {
    private String loginId;

    public LoginId(final String loginId) {
        final String USER_ID_REGEX = "^[a-zA-Z0-9]{1,10}$";

        if (!StringUtils.hasText(loginId)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 비어있을 수 없습니다.");
        }

        if (!loginId.matches(USER_ID_REGEX)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 10자를 넘을 수 없습니다.");
        }

        this.loginId = loginId;
    }
}
