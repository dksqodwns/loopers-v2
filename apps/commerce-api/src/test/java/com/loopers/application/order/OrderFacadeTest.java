package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.application.order.OrderCriteria.Order;
import com.loopers.domain.brand.Brand;
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
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.stock.ProductStockJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.hikari.maximum-pool-size=40"
})
public class OrderFacadeTest {

    private final OrderFacade orderFacade;
     private final UserJpaRepository userRepo;
     private final PointJpaRepository pointRepo;
     private final BrandJpaRepository brandRepo;
     private final ProductJpaRepository productRepo;
     private final ProductStockJpaRepository stockRepo;

     private final DatabaseCleanUp clean;

     @Autowired
    public OrderFacadeTest(OrderFacade orderFacade, UserJpaRepository userRepo, PointJpaRepository pointRepo,
                           BrandJpaRepository brandRepo, ProductJpaRepository productRepo,
                           ProductStockJpaRepository stockRepo, DatabaseCleanUp clean) {
        this.orderFacade = orderFacade;
        this.userRepo = userRepo;
        this.pointRepo = pointRepo;
        this.brandRepo = brandRepo;
        this.productRepo = productRepo;
        this.stockRepo = stockRepo;
        this.clean = clean;
    }

    private Long userId;
    private Long productId;

    private final int initialStock = 100;

    @BeforeEach
    void setUp() {
        User user = userRepo.save(new User(
                new LoginId("test"),
                new Email("test@test.com"),
                "테스터",
                new BirthDate("1998-01-08"),
                Gender.from("MALE")
        ));
        userId = user.getId();
        pointRepo.save(new Point(userId, new Amount(1_000_000L)));

        Brand brand = brandRepo.save(new Brand("브랜드"));
        Product product = productRepo.save(new Product(new BrandId(brand.getId()), "동시성 상품", 1_000L));
        productId = product.getId();

        stockRepo.save(new ProductStock(productId, new StockQuantity(initialStock)));
    }

    @AfterEach
    void tearDown() {
        clean.truncateAllTables();
    }

    @DisplayName("같은 상품에 대한 동시 주문이 과판매를 발생시키지 않는다")
    @Test
    void concurrent_orders_should_not_oversell() throws Exception {
        int threads = 20;
        int qtyPerOrder = 10;

        int expectedSuccess = Math.min(initialStock / qtyPerOrder, threads);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail    = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    Order criteria = new Order(
                            userId,
                            List.of(new Order.OrderItem(productId, qtyPerOrder)));

                    orderFacade.order(criteria);
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(10, TimeUnit.SECONDS);
        start.countDown();

        boolean finished = done.await(60, TimeUnit.SECONDS);
        pool.shutdownNow();

        assertThat(finished).isTrue();

        ProductStock ps = stockRepo.findByProductId(productId).orElseThrow();
        int finalStock = ps.getStock().getStock();

        // 성공 주문 수는 재고 한도를 넘지 않아야 함
        assertThat(success.get())
                .isEqualTo(expectedSuccess);

        // 최종 재고는 초기재고 - (성공건수 × 주문수량)
        assertThat(finalStock)
                .isEqualTo(initialStock - success.get() * qtyPerOrder);

        assertThat(finalStock).isGreaterThanOrEqualTo(0);
        assertThat(fail.get()).isEqualTo(threads - success.get());
    }
}
