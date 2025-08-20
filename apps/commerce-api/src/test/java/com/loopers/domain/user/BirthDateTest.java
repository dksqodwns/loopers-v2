package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

public class BirthDateTest {

    @DisplayName("생년월일을 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("생년월일 형식이 yyyy-dd-mm이 아니면, 400 Bad Request를 반환한다.")
        @Test
        void retunr400BadRequest_whenInvalidFormat() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new BirthDate("08/01/1998"));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 NULL이면, 400 Bad Requset를 반환한다.")
        @NullSource
        @ParameterizedTest
        void return400BadRequest_whenNULL(final String birthDate) {

            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new BirthDate(birthDate));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 과거가 아니라면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenFutureBirthDate() {
            String birthDate = LocalDate.now().plusDays(1).toString();
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> new BirthDate(birthDate));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
