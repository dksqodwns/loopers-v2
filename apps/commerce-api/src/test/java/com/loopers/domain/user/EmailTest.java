package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

public class EmailTest {

    @DisplayName("이메일을 생성 할 때,")
    @Nested
    class Create {
        @DisplayName("이메일 형식이 아니면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenInvalidEmail() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new Email("test"));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 NULL이면, 400 Bad Request를 반환한다.")
        @NullSource
        @ParameterizedTest
        void return400BadRequest_whenNull(final String email) {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new Email(email));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}

