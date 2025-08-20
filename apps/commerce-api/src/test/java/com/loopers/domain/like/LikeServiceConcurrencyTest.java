package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.infrastructure.like.LikeJpaRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LikeServiceConcurrencyTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private LikeJpaRepository likeJpaRepository;

    @AfterEach
    void tearDown() {
        likeJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동시에 100개의 좋아요 요청이 들어와도 한번만 저장되어야 한다")
    void like_concurrency() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        LikeCommand.Like command = new LikeCommand.Like(1L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L));

        // when
        IntStream.range(0, threadCount).forEach(i -> executorService.submit(() -> {
            try {
                likeService.like(command);
            } finally {
                latch.countDown();
            }
        }));

        latch.await();
        executorService.shutdown();

        // then
        long count = likeRepository.findAllBy(command.userId(), command.target().getTargetType()).size();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("한 사용자가 좋아요를 누른 상품을 다른 사용자가 동시에 좋아요를 누를 경우, 두개의 좋아요가 저장되어야 한다")
    void like_concurrency_with_different_users() throws InterruptedException {
        // given
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(threadCount);

        LikeCommand.Like command1 = new LikeCommand.Like(1L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L));
        LikeCommand.Like command2 = new LikeCommand.Like(2L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L));

        // when
        executorService.submit(() -> {
            try {
                likeService.like(command1);
            } finally {
                latch.countDown();
            }
        });
        executorService.submit(() -> {
            try {
                likeService.like(command2);
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executorService.shutdown();

        // then
        long count = likeRepository.findAllBy(1L, LikeTarget.TargetType.PRODUCT).size();
        long count2 = likeRepository.findAllBy(2L, LikeTarget.TargetType.PRODUCT).size();
        assertThat(count).isEqualTo(1);
        assertThat(count2).isEqualTo(1);
    }
}
