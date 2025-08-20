package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisTemplate<String, String> redis;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("getProduct 메서드는")
    class GetProduct {

        @BeforeEach
        void setupCache() {
            given(redis.opsForValue()).willReturn(valueOps);
        }

        @Test
        @DisplayName("캐시 미스 시 DB에서 조회 후 캐시에 저장하고 반환한다")
        void getProduct_cacheMiss_dbThenCache() throws Exception {
            // given
            Long productId = 1L;
            String key = "product:detail:" + productId;
            given(valueOps.get(key)).willReturn(null);

            Product product = new Product(new BrandId(1L), "Test Product", 10000L);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            ProductInfo productInfo = productService.getProduct(new ProductCommand.GetProduct(productId));

            // then
            assertThat(productInfo).isNotNull();
            assertThat(productInfo.name()).isEqualTo("Test Product");
            verify(valueOps).set(eq(key), anyString(), any(Duration.class));
        }

        @Test
        @DisplayName("캐시 히트 시 DB를 조회하지 않고 캐시에서 반환한다")
        void getProduct_cacheHit_returnsFromCache() throws Exception {
            // given
            Long productId = 2L;
            String key = "product:detail:" + productId;

            Product cached = new Product(new BrandId(1L), "Cached Product", 5000L);
            String cachedJson = objectMapper.writeValueAsString(ProductInfo.from(cached));
            given(valueOps.get(key)).willReturn(cachedJson);

            // when
            ProductInfo productInfo = productService.getProduct(new ProductCommand.GetProduct(productId));

            // then
            assertThat(productInfo).isNotNull();
            assertThat(productInfo.name()).isEqualTo("Cached Product");
            verify(productRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID면 CoreException을 던진다")
        void getProduct_notFound_throwsCoreException() {
            // given
            Long productId = 99L;
            String key = "product:detail:" + productId;
            given(valueOps.get(key)).willReturn(null);
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when & then
            CoreException ex = assertThrows(CoreException.class,
                    () -> productService.getProduct(new ProductCommand.GetProduct(productId)));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(valueOps, never()).set(eq(key), anyString(), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("getProductById 메서드는")
    class GetProductById {

        @Test
        @DisplayName("존재하는 상품 ID로 조회하면 Optional<ProductInfo>를 반환한다")
        void getProductById_withExistingId_returnsOptionalProductInfo() {
            Long productId = 1L;
            Product product = new Product(new BrandId(1L), "Test Product", 10000L);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            Optional<ProductInfo> productInfo = productService.getProductById(new ProductCommand.GetProduct(productId));

            assertThat(productInfo).isPresent();
            assertThat(productInfo.get().name()).isEqualTo("Test Product");
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회하면 빈 Optional을 반환한다")
        void getProductById_withNonExistingId_returnsEmptyOptional() {
            Long productId = 1L;
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            Optional<ProductInfo> productInfo = productService.getProductById(new ProductCommand.GetProduct(productId));

            assertThat(productInfo).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchProducts 메서드는")
    class SearchProducts {

        @BeforeEach
        void setupCache() {
            given(redis.opsForValue()).willReturn(valueOps);
            lenient().when(valueOps.get(anyString())).thenReturn(null);
        }

        @Test
        @DisplayName("브랜드 ID가 주어지면 해당 브랜드의 상품 목록을 페이지로 반환한다 (캐시 미스)")
        void searchProducts_withBrandId_returnsPagedProductInfo() {
            Long brandId = 1L;
            PageRequest pageRequest = PageRequest.of(0, 10);
            List<Product> products = Collections.singletonList(new Product(new BrandId(brandId), "Test Product", 10000L));
            Page<Product> pagedProducts = new PageImpl<>(products, pageRequest, products.size());
            given(productRepository.findAllBy(brandId, pageRequest)).willReturn(pagedProducts);

            Page<ProductInfo> result = productService.searchProducts(new ProductCommand.SearchProducts(brandId, pageRequest));

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getBrandId()).isEqualTo(brandId);
        }

        @Test
        @DisplayName("브랜드 ID가 없으면 모든 상품 목록을 페이지로 반환한다 (캐시 미스)")
        void searchProducts_withoutBrandId_returnsPagedProductInfo() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            List<Product> products = Collections.singletonList(new Product(new BrandId(1L), "Test Product", 10000L));
            Page<Product> pagedProducts = new PageImpl<>(products, pageRequest, products.size());
            given(productRepository.findAllBy(pageRequest)).willReturn(pagedProducts);

            Page<ProductInfo> result = productService.searchProducts(new ProductCommand.SearchProducts(null, pageRequest));

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getProducts 메서드는")
    class GetProducts {

        @Test
        @DisplayName("상품 ID 목록으로 조회하면 상품 정보 목록을 반환한다")
        void getProducts_withIds_returnsListOfProductInfo() {
            List<Long> ids = List.of(1L, 2L);
            List<Product> products = List.of(
                    new Product(new BrandId(1L), "Product 1", 10000L),
                    new Product(new BrandId(1L), "Product 2", 20000L)
            );
            given(productRepository.findAllByIds(ids)).willReturn(products);

            List<ProductInfo> result = productService.getProducts(new ProductCommand.GetProducts(ids));

            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Product 1");
            assertThat(result.get(1).name()).isEqualTo("Product 2");
        }
    }
}
