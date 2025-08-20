package com.loopers.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ProductStockTest {

    @DisplayName("상품 재고를 생성 할 때,")
    @Nested
    class Create {

        @DisplayName("상품 ID가 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenProductIdIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new ProductStock(null, new StockQuantity(100))
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("재고 수량이 없으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenStockQuantityIsNull() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new ProductStock(1L, null)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정상적인 값으로 생성하면, 상품 재고가 생성된다.")
        @Test
        void createProductStock() {
            ProductStock productStock = new ProductStock(1L, new StockQuantity(100));

            Assertions.assertAll(
                    () -> assertThat(productStock.getProductId()).isEqualTo(1L),
                    () -> assertThat(productStock.getStock().getStock()).isEqualTo(100)
            );
        }
    }

    @DisplayName("재고 수량을 감소 시킬 때,")
    @Nested
    class Decrease {

        @DisplayName("감소 시킬 수량이 현재 재고보다 많으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenQuantityIsGreaterThanCurrentStock() {
            ProductStock productStock = new ProductStock(1L, new StockQuantity(10));

            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    productStock.decrease(20)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정상적으로 감소 시키면, 재고 수량이 감소한다.")
        @Test
        void decreaseStockQuantity() {
            ProductStock productStock = new ProductStock(1L, new StockQuantity(100));
            productStock.decrease(20);

            assertThat(productStock.getStock().getStock()).isEqualTo(80);
        }
    }

    @DisplayName("StockQuantity를 생성 할 때,")
    @Nested
    class StockQuantityCreate {

        @DisplayName("수량이 음수이면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenQuantityIsNegative() {
            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    new StockQuantity(-1)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("StockQuantity를 감소 시킬 때,")
    @Nested
    class StockQuantityDecrease {

        @DisplayName("감소 시킬 수량이 현재 재고보다 많으면, 400 Bad Request를 반환한다.")
        @Test
        void return400BadRequest_whenQuantityIsGreaterThanCurrentStock() {
            StockQuantity stockQuantity = new StockQuantity(10);

            CoreException coreException = Assertions.assertThrows(CoreException.class, () ->
                    stockQuantity.decrease(20)
            );

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정상적으로 감소 시키면, 재고 수량이 감소한다.")
        @Test
        void decreaseStockQuantity() {
            StockQuantity stockQuantity = new StockQuantity(100);
            stockQuantity.decrease(20);

            assertThat(stockQuantity.getStock()).isEqualTo(80);
        }
    }
}
