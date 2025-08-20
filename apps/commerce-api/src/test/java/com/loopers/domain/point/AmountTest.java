package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AmountTest {

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {

        @DisplayName("100원 이하가 충전되면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenChargeUnder100Point() {
            Point point = new Point(1L, new Amount(0L));
            CoreException coreException = Assertions.assertThrows(
                    CoreException.class, (
                    ) -> point.charge(99L)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("100원이상이 충전되면, 정상적으로 충전된다.")
        @Test
        void chargePoint() {
            Point point = new Point(1L, new Amount(0L));
            point.charge(1000L);

            assertThat(point.getAmount().getAmount()).isEqualTo(1000L);
        }
    }
}
