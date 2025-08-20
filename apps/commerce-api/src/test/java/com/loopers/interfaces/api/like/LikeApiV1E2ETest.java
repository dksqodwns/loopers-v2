package com.loopers.interfaces.api.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeTarget;
import com.loopers.domain.product.BrandId;
import com.loopers.domain.product.Product;
import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.like.LikeDto.V1.GetLikeProductsResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LikeApiV1E2ETest {

    public static final String END_POINT = "/api/v1/like";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private LikeJpaRepository likeJpaRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(new User(new LoginId("test"), new Email("test@test.com"), "test", new BirthDate("1998-01-08"), Gender.MALE));
        Brand brand = brandJpaRepository.save(new Brand("test brand"));
        product = productJpaRepository.save(new Product(new BrandId(brand.getId()), "test product", 10000L));
    }

    @AfterEach
    public void cleanUp() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품을 좋아요 할 때(POST),")
    @Nested
    class LikeProduct {

        @DisplayName("성공할 경우, 200 OK를 반환하고 좋아요가 취소된다.")
        @Test
        void returnSuccess_whenLikeProduct() {
            // given
            likeJpaRepository.save(new Like(user.getId(), new LikeTarget(LikeTarget.TargetType.PRODUCT, product.getId())));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());
            HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

            // when
            ResponseEntity<ApiResponse<Object>> response = testRestTemplate.exchange(
                    END_POINT + "/products/" + product.getId(), HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
                    }
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS)
            );

            boolean isLiked = likeJpaRepository.existsByUserIdAndLikeTarget(user.getId(), new LikeTarget(LikeTarget.TargetType.PRODUCT, product.getId()));
            assertThat(isLiked).isFalse();
        }
    }

    @DisplayName("상품 좋아요를 취소 할 때(DELETE),")
    @Nested
    class UnlikeProduct {

        @DisplayName("성공할 경우, 200 OK를 반환하고 좋아요가 생성된다.")
        @Test
        void returnSuccess_whenUnlikeProduct() {
            // given
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());
            HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

            // when
            ResponseEntity<ApiResponse<Object>> response = testRestTemplate.exchange(
                    END_POINT + "/products/" + product.getId(), HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {
                    }
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS)
            );

            boolean isLiked = likeJpaRepository.existsByUserIdAndLikeTarget(user.getId(), new LikeTarget(LikeTarget.TargetType.PRODUCT, product.getId()));
            assertThat(isLiked).isTrue();
        }
    }

    @DisplayName("좋아요한 상품 목록을 조회 할 때,")
    @Nested
    class GetLikeProducts {

        @BeforeEach
        void setUp() {
            likeJpaRepository.save(new Like(user.getId(), new LikeTarget(LikeTarget.TargetType.PRODUCT, product.getId())));
        }

        @DisplayName("성공할 경우, 좋아요한 상품 목록을 반환한다.")
        @Test
        void returnLikedProducts_whenGetLikeProducts() {
            // given
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());
            HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

            // when
            ResponseEntity<ApiResponse<GetLikeProductsResponse>> response = testRestTemplate.exchange(
                    END_POINT + "/products", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                    }
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(response.getBody().data().likeProducts()).hasSize(1),
                    () -> assertThat(response.getBody().data().likeProducts().get(0).id()).isEqualTo(product.getId())
            );
        }
    }
}
