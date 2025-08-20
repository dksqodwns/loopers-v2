package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class GenderTest {

    @DisplayName("성별을 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("성별이 Male,Female이 아니면 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenInvalidGender() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> Gender.from("Android"));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("성별이 비어있으면, 400 Bad Request를 반환한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void return400BadRequest_whenNullGender(final String gender) {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> Gender.from(gender));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
