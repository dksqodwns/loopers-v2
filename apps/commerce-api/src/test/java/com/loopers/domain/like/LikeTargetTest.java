package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LikeTargetTest {

    @DisplayName("LikeTarget을 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("타겟 ID가 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenTargetIdIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new LikeTarget(LikeTarget.TargetType.PRODUCT, null)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("타겟 타입이 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenTargetTypeIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new LikeTarget(null, 1L)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정상적인 값으로 생성하면, LikeTarget이 생성된다.")
        @Test
        void createLikeTarget() {
            LikeTarget likeTarget = new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L);

            Assertions.assertAll(
                    () -> assertThat(likeTarget.getTargetType()).isEqualTo(LikeTarget.TargetType.PRODUCT),
                    () -> assertThat(likeTarget.getId()).isEqualTo(1L)
            );
        }
    }

    @DisplayName("TargetType을 생성 할 때,")
    @Nested
    class CreateTargetType {

        @DisplayName("존재하지 않는 타입이면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenInvalidTargetType() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    LikeTarget.TargetType.from("INVALID_TYPE")
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("타입이 null이면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenTargetTypeIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    LikeTarget.TargetType.from(null)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
