package com.loopers.domain.count;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductCountServiceTest {

    @InjectMocks
    private ProductCountService productCountService;

    @Mock
    private ProductCountRepository productCountRepository;

    @Nested
    @DisplayName("increase 메서드는")
    class Increase {

        @Test
        @DisplayName("존재하는 상품의 좋아요 카운트를 1 증가시킨다")
        void increase_likeCount_forExistingProduct() {
            // given
            Long productId = 1L;
            ProductCount productCount = new ProductCount(productId);
            given(productCountRepository.findByProductId(productId)).willReturn(Optional.of(productCount));

            // when
            productCountService.increse(new ProductCountCommand.Increase(productId, ProductCount.CountType.LIKE));

            // then
            assertThat(productCount.getLikeCount().getCount()).isEqualTo(1L);
            verify(productCountRepository, times(1)).save(productCount);
        }

        @Test
        @DisplayName("존재하지 않는 상품의 좋아요 카운트를 1로 초기화한다")
        void increase_likeCount_forNonExistingProduct() {
            // given
            Long productId = 1L;
            given(productCountRepository.findByProductId(productId)).willReturn(Optional.empty());
            given(productCountRepository.save(any(ProductCount.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            productCountService.increse(new ProductCountCommand.Increase(productId, ProductCount.CountType.LIKE));

            // then
            verify(productCountRepository, times(1)).save(any(ProductCount.class));
        }
    }

    @Nested
    @DisplayName("decrease 메서드는")
    class Decrease {

        @Test
        @DisplayName("존재하는 상품의 좋아요 카운트를 1 감소시킨다")
        void decrease_likeCount_forExistingProduct() {
            // given
            Long productId = 1L;
            ProductCount productCount = new ProductCount(productId);
            productCount.increase(ProductCount.CountType.LIKE);
            given(productCountRepository.findByProductId(productId)).willReturn(Optional.of(productCount));

            // when
            productCountService.decrese(new ProductCountCommand.Decrease(productId, ProductCount.CountType.LIKE));

            // then
            assertThat(productCount.getLikeCount().getCount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("getProductCounts 메서드는")
    class GetProductCounts {

        @Test
        @DisplayName("상품 ID 목록으로 조회하면 상품 카운트 정보 목록을 반환한다")
        void getProductCounts_withProductIds_returnsListOfProductCountInfo() {
            // given
            List<Long> productIds = List.of(1L, 2L);
            List<ProductCount> productCounts = List.of(
                    new ProductCount(1L),
                    new ProductCount(2L)
            );
            given(productCountRepository.findAllByProductIds(productIds)).willReturn(productCounts);

            // when
            List<ProductCountInfo> result = productCountService.getProductCounts(new ProductCountCommand.GetProductCounts(productIds));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).productId()).isEqualTo(1L);
            assertThat(result.get(1).productId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("getProductCount 메서드는")
    class GetProductCount {

        @Test
        @DisplayName("존재하는 상품 ID로 조회하면 Optional<ProductCountInfo>를 반환한다")
        void getProductCount_withExistingProductId_returnsOptionalProductCountInfo() {
            // given
            Long productId = 1L;
            ProductCount productCount = new ProductCount(productId);
            given(productCountRepository.findByProductId(productId)).willReturn(Optional.of(productCount));

            // when
            Optional<ProductCountInfo> result = productCountService.getProductCount(new ProductCountCommand.GetProductCount(productId));

            // then
            assertThat(result).isPresent();
            assertThat(result.get().productId()).isEqualTo(productId);
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회하면 빈 Optional을 반환한다")
        void getProductCount_withNonExistingProductId_returnsEmptyOptional() {
            // given
            Long productId = 1L;
            given(productCountRepository.findByProductId(productId)).willReturn(Optional.empty());

            // when
            Optional<ProductCountInfo> result = productCountService.getProductCount(new ProductCountCommand.GetProductCount(productId));

            // then
            assertThat(result).isEmpty();
        }
    }
}
