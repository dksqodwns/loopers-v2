package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @DisplayName("포인트를 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("회원의 포인트가 있는 경우, 409 Conflict를 반환한다.")
        @Test
        void return409Conflict_whenExistsPoint() {
            given(pointRepository.existsByUserId(ArgumentMatchers.anyLong())).willReturn(true);

            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> pointService.create(new PointCommand.Create(1L, 100L)));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("포인트 정보를 조회 할 때,")
    @Nested
    class Get {

        @DisplayName("존재하지 않는 회원의 id가 주어지면, 404 Not Found를 반환한다.")
        @Test
        void return404NotFound_whenGetNotExistsUserId() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> pointService.getPoint(777L));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트를 충전 할 때,")
    @Nested
    class Charge {

        @DisplayName("존재하지 않는 회원의 id가 주어지면, 404 Not Found를 반환한다.")
        @Test
        void return404NotFound_whenChargeNotExistsUserId() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () -> pointService.charge(new PointCommand.Charge(777L, 100L)));

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

}
