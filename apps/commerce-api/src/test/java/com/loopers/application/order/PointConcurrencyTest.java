package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
public class PointConcurrencyTest {


    private OrderFacade orderFacade;

    private UserJpaRepository userRepo;
    private PointJpaRepository pointRepo;
    private BrandJpaRepository brandRepo;
    private ProductJpaRepository productRepo;
    private ProductStockJpaRepository stockRepo;

    private DatabaseCleanUp clean;

    @Autowired
    public PointConcurrencyTest(OrderFacade orderFacade, UserJpaRepository userRepo, PointJpaRepository pointRepo,
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

    @PersistenceContext
    private EntityManager em;

    private Long userId;
    private Long productAId;
    private Long productBId;

    private final long initialPoints = 300_000L;
    private final long priceA = 1_000L;
    private final long priceB = 1_500L;

    @BeforeEach
    void setUp() {
        User user = userRepo.save(new User(
                new LoginId("test"),
                new Email("test@test.com"),
                "테스터",
                new BirthDate("1990-01-01"),
                Gender.from("MALE")
        ));
        userId = user.getId();
        pointRepo.save(new Point(userId, new Amount(initialPoints)));

        Brand brand = brandRepo.save(new Brand("브랜드"));
        Product a = productRepo.save(new Product(new BrandId(brand.getId()), "상품A", priceA));
        Product b = productRepo.save(new Product(new BrandId(brand.getId()), "상품B", priceB));
        productAId = a.getId();
        productBId = b.getId();

        stockRepo.save(new ProductStock(productAId, new StockQuantity(10_000)));
        stockRepo.save(new ProductStock(productBId, new StockQuantity(10_000)));

        assertThat(userId).isNotNull();
        assertThat(productAId).isNotNull();
        assertThat(productBId).isNotNull();
    }

    @AfterEach
    void tearDown() {
        clean.truncateAllTables();
    }

    @Test
    @DisplayName("동일 유저의 동시 다중 주문에도 포인트가 정확히 차감된다")
    void concurrent_orders_deduct_points_correctly() throws Exception {
        int threads = 12;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();

                    OrderCriteria.Order criteria =
                            (idx % 2 == 0)
                                    ? new OrderCriteria.Order(userId, List.of(new OrderCriteria.Order.OrderItem(productAId, 50)))
                                    : new OrderCriteria.Order(userId, List.of(new OrderCriteria.Order.OrderItem(productBId, 40)));

                    orderFacade.order(criteria); // 내부에서 결제(Payment) → 포인트 use()
                } catch (Exception ignored) {
                    // 포인트 부족/경합 실패는 허용 (검증은 아래에서 보전식으로)
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(10, TimeUnit.SECONDS);
        start.countDown();
        done.await(60, TimeUnit.SECONDS);
        pool.shutdownNow();

        // 완료 결제 합
        long sumPaid = ((Number) em.createNativeQuery(
                        "select coalesce(sum(total_price), 0) " +
                                "from payments where status = 'COMPLETED' and ref_user_id = ?1")
                .setParameter(1, userId)
                .getSingleResult()
        ).longValue();

        // 최종 포인트
        long finalPoints = ((Number) em.createNativeQuery(
                        "select amount from points where user_id = ?1")
                .setParameter(1, userId)
                .getSingleResult()
        ).longValue();

        // 보전식
        assertThat(initialPoints - finalPoints)
                .as("포인트 차감 합계 == 완료 결제 금액 합")
                .isEqualTo(sumPaid);

        assertThat(finalPoints).as("최종 포인트는 음수 불가").isGreaterThanOrEqualTo(0);
        assertThat(sumPaid).as("완료 결제 합은 초기 포인트를 넘을 수 없음").isLessThanOrEqualTo(initialPoints);
    }
}
