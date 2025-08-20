package com.loopers.interfaces.api.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.point.Amount;
import com.loopers.domain.point.Point;
import com.loopers.domain.product.BrandId;
import com.loopers.domain.product.Product;
import com.loopers.domain.stock.ProductStock;
import com.loopers.domain.stock.StockQuantity;
import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.count.ProductCountJpaRepository;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.stock.ProductStockJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.OrderDto.V1.OrderItemRequest;
import com.loopers.interfaces.api.order.OrderDto.V1.OrderRequest;
import com.loopers.interfaces.api.order.OrderDto.V1.OrdersResponse;
import com.loopers.utils.DatabaseCleanUp;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
public class OrderApiV1E2ETest {

    public final String END_POINT = "/api/v1/orders";

    private TestRestTemplate testRestTemplate;
    private OrderJpaRepository orderJpaRepository;
    private ProductJpaRepository productJpaRepository;
    private UserJpaRepository userJpaRepository;
    private ProductStockJpaRepository productStockJpaRepository;
    private ProductCountJpaRepository productCountJpaRepository;
    private PointJpaRepository pointJpaRepository;
    private BrandJpaRepository brandJpaRepository;
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    public OrderApiV1E2ETest(
            TestRestTemplate testRestTemplate, OrderJpaRepository orderJpaRepository,
            ProductJpaRepository productJpaRepository, UserJpaRepository userJpaRepository,
            ProductStockJpaRepository productStockJpaRepository,
            ProductCountJpaRepository productCountJpaRepository, PointJpaRepository pointJpaRepository,
            BrandJpaRepository brandJpaRepository,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.orderJpaRepository = orderJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.productStockJpaRepository = productStockJpaRepository;
        this.productCountJpaRepository = productCountJpaRepository;
        this.pointJpaRepository = pointJpaRepository;
        this.brandJpaRepository = brandJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    User user;
    Point point;
    Brand brand;
    Product product1;
    Product product2;
    Product product3;
    ProductStock productStock1;
    ProductStock productStock2;
    ProductStock productStock3;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(
                new User(new LoginId("test"), new Email("test@test.com"), "안병준", new BirthDate("1998-01-08"),
                        Gender.from("MALE")));
        point = pointJpaRepository.save(new Point(user.getId(), new Amount(10000000L)));
        brand = brandJpaRepository.save(new Brand("테스트 브랜드"));
        product1 = productJpaRepository.save(new Product(new BrandId(1L), "테스트 상품 1", 1_000L));
        product2 = productJpaRepository.save(new Product(new BrandId(1L), "테스트 상품 2", 2_000L));
        product3 = productJpaRepository.save(new Product(new BrandId(1L), "테스트 상품 3", 3_000L));
        productStock1 = productStockJpaRepository.save(
                new ProductStock(product1.getId(), new StockQuantity(100)));
        productStock2 = productStockJpaRepository.save(
                new ProductStock(product2.getId(), new StockQuantity(100)));
        productStock3 = productStockJpaRepository.save(
                new ProductStock(product3.getId(), new StockQuantity(100)));
    }

    @AfterEach
    public void tearDown() {
        this.databaseCleanUp.truncateAllTables();
    }

    @DisplayName("주문 요청 시")
    @Nested
    class PlaceOrder {

        @DisplayName("성공 할 경우, 정상적으로 주문이 요청된다.")
        @Test
        void returnSuccess_whenOrderSuccessful() {

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());
            System.out.println("id: "+user.getId());
            System.out.println("hdears: "+headers.get("X-USER-ID"));

            OrderItemRequest orderItemRequest1 = new OrderItemRequest(product1.getId(), 1);
            OrderItemRequest orderItemRequest2 = new OrderItemRequest(product2.getId(), 2);
            OrderItemRequest orderItemRequest3 = new OrderItemRequest(product3.getId(), 3);
            List<OrderItemRequest> orderItemRequests = Arrays.asList(
                    orderItemRequest1,
                    orderItemRequest2,
                    orderItemRequest3
            );

            OrderDto.V1.OrderRequest orderRequest = new OrderRequest(orderItemRequests);
            HttpEntity<OrderRequest> request = new HttpEntity<>(orderRequest, headers);
            ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<Object>> response = testRestTemplate.exchange(
                    END_POINT, HttpMethod.POST, request, responseType
            );

            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(
                            ApiResponse.Metadata.Result.SUCCESS
                    )
            );
        }

    }

    @DisplayName("주문 목록 조회 시")
    @Nested
    class GetOrdersTest {
        @DisplayName("주문 목록이 반환된다.")
        @Test
        void returnSuccess_whenGetOrderList() {
            List<OrderItem> orderItems = Arrays.asList(
                    new OrderItem(product1.getId(), product1.getPrice(), 1),
                    new OrderItem(product2.getId(), product2.getPrice(), 2),
                    new OrderItem(product3.getId(), product3.getPrice(), 3)
            );
            Order order = new Order(user.getId(), orderItems);
            orderJpaRepository.save(order);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());
            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            ParameterizedTypeReference<ApiResponse<OrdersResponse>> responseType = new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<OrdersResponse>> response = testRestTemplate.exchange(
                    END_POINT, HttpMethod.GET, request, responseType
            );


            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(
                            ApiResponse.Metadata.Result.SUCCESS
                    )
            );
        }
    }
}
