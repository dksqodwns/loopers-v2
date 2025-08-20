package com.loopers.domain.count;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ProductCountTest {

    @DisplayName("상품 카운트를 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("상품 ID가 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenProductIdIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new ProductCount(null)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정상적인 값으로 생성하면, 상품 카운트가 생성된다.")
        @Test
        void createProductCount() {
            ProductCount productCount = new ProductCount(1L);

            Assertions.assertAll(
                    () -> assertThat(productCount.getProductId()).isEqualTo(1L),
                    () -> assertThat(productCount.getLikeCount().getCount()).isEqualTo(0L)
            );
        }
    }

    @DisplayName("상품 카운트를 증가 시킬 때,")
    @Nested
    class Increase {

        @DisplayName("좋아요 카운트가 증가한다.")
        @Test
        void increaseLikeCount() {
            ProductCount productCount = new ProductCount(1L);
            productCount.increase(ProductCount.CountType.LIKE);

            assertThat(productCount.getLikeCount().getCount()).isEqualTo(1L);
        }
    }

    @DisplayName("상품 카운트를 감소 시킬 때,")
    @Nested
    class Decrease {

        @DisplayName("좋아요 카운트가 감소한다.")
        @Test
        void decreaseLikeCount() {
            ProductCount productCount = new ProductCount(1L);
            productCount.increase(ProductCount.CountType.LIKE);
            productCount.decrease(ProductCount.CountType.LIKE);

            assertThat(productCount.getLikeCount().getCount()).isEqualTo(0L);
        }

        @DisplayName("좋아요 카운트가 0이면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenLikeCountIsZero() {
            ProductCount productCount = new ProductCount(1L);

            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    productCount.decrease(ProductCount.CountType.LIKE)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("Count를 생성 할 때,")
    @Nested
    class CountCreate {

        @DisplayName("카운트가 NULL이면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenCountIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new ProductCount.Count(null)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("카운트가 음수이면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenCountIsNegative() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new ProductCount.Count(-1L)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("Count를 감소 시킬 때,")
    @Nested
    class CountDecrease {

        @DisplayName("카운트가 0이면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenCountIsZero() {
            ProductCount.Count count = new ProductCount.Count(0L);

            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    count.decrease()
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
