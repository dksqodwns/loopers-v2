package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ProductTest {

    @DisplayName("상품을 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("상품 이름이 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenNameIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new Product(new BrandId(1L), null, 10000L)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("브랜드 ID가 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenBrandIdIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new Product(null, "test product", 10000L)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정상적인 값으로 생성하면, 상품이 생성된다.")
        @Test
        void createProduct() {
            Product product = new Product(new BrandId(1L), "test product", 10000L);

            Assertions.assertAll(
                    () -> assertThat(product.getBrandId().getBrandId()).isEqualTo(1L),
                    () -> assertThat(product.getName()).isEqualTo("test product"),
                    () -> assertThat(product.getPrice()).isEqualTo(10000L)
            );
        }
    }
}
