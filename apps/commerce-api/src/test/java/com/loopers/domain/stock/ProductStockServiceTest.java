package com.loopers.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductStockServiceTest {

    @InjectMocks
    private ProductStockService productStockService;

    @Mock
    private ProductStockRepository productStockRepository;

    @Nested
    @DisplayName("findStock 메서드는")
    class FindStock {

        @Test
        @DisplayName("존재하는 상품 ID로 조회하면 Optional<ProductStockInfo>를 반환한다")
        void findStock_withExistingProductId_returnsOptionalProductStockInfo() {
            // given
            Long productId = 1L;
            ProductStock productStock = new ProductStock(productId, new StockQuantity(100));
            given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(productStock));

            // when
            Optional<ProductStockInfo> result = productStockService.findStock(new ProductStockCommand.GetStock(productId));

            // then
            assertThat(result).isPresent();
            assertThat(result.get().productId()).isEqualTo(productId);
            assertThat(result.get().getStock()).isEqualTo(100);
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회하면 빈 Optional을 반환한다")
        void findStock_withNonExistingProductId_returnsEmptyOptional() {
            // given
            Long productId = 1L;
            given(productStockRepository.findByProductId(productId)).willReturn(Optional.empty());

            // when
            Optional<ProductStockInfo> result = productStockService.findStock(new ProductStockCommand.GetStock(productId));

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getStocks 메서드는")
    class GetStocks {

        @Test
        @DisplayName("상품 ID 목록으로 조회하면 상품 재고 정보 목록을 반환한다")
        void getStocks_withProductIds_returnsListOfProductStockInfo() {
            // given
            List<Long> productIds = List.of(1L, 2L);
            List<ProductStock> productStocks = List.of(
                    new ProductStock(1L, new StockQuantity(100)),
                    new ProductStock(2L, new StockQuantity(200))
            );
            given(productStockRepository.findAllByProductId(productIds)).willReturn(productStocks);

            // when
            List<ProductStockInfo> result = productStockService.getStocks(new ProductStockCommand.GetStocks(productIds));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).productId()).isEqualTo(1L);
            assertThat(result.get(0).getStock()).isEqualTo(100);
            assertThat(result.get(1).productId()).isEqualTo(2L);
            assertThat(result.get(1).getStock()).isEqualTo(200);
        }
    }
}