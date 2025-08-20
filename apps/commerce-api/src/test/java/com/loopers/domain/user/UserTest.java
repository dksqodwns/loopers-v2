package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserTest {

    @DisplayName("유저를 생성 할 때,")
    @Nested
    class Create {
        @DisplayName("성별이 없으면 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenCreateWithoutGender() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> {
                new User(
                        new LoginId("test"),
                        new Email("test@test.com"),
                        "안병준",
                        new BirthDate("1998-01-08"),
                        null
                );
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

}
