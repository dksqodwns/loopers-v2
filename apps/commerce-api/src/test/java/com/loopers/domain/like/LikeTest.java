package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LikeTest {

    @DisplayName("Like를 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("유저 ID가 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenUserIdIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new Like(null, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L))
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("LikeTarget이 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenLikeTargetIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new Like(1L, null)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정상적인 값으로 생성하면, Like가 생성된다.")
        @Test
        void createLike() {
            Like like = new Like(1L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L));

            Assertions.assertAll(
                    () -> assertThat(like.getUserId()).isEqualTo(1L),
                    () -> assertThat(like.getLikeTarget().getTargetType()).isEqualTo(LikeTarget.TargetType.PRODUCT),
                    () -> assertThat(like.getLikeTarget().getId()).isEqualTo(1L)
            );
        }
    }
}
