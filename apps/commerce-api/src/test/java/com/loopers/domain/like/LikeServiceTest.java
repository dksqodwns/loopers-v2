package com.loopers.domain.like;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Nested
    @DisplayName("like 메서드는")
    class Like {

        @Test
        @DisplayName("repository의 saveIfAbsent를 호출하고 결과를 반환한다")
        void callSaveIfAbsentAndReturnResult() {
            // given
            LikeCommand.Like command = new LikeCommand.Like(1L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L));
            given(likeRepository.saveIfAbsent(any(com.loopers.domain.like.Like.class))).willReturn(true);

            // when
            boolean result = likeService.like(command);

            // then
            assertThat(result).isTrue();
            verify(likeRepository).saveIfAbsent(any(com.loopers.domain.like.Like.class));
        }
    }

    @Nested
    @DisplayName("unlike 메서드는")
    class Unlike {

        @Test
        @DisplayName("repository의 deleteBy를 호출하고 결과를 반환한다")
        void callDeleteByAndReturnResult() {
            // given
            LikeCommand.Unlike command = new LikeCommand.Unlike(1L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L));
            given(likeRepository.deleteBy(command.userId(), command.target())).willReturn(1L);

            // when
            boolean result = likeService.unlike(command);

            // then
            assertThat(result).isTrue();
            verify(likeRepository).deleteBy(command.userId(), command.target());
        }

        @Test
        @DisplayName("삭제된 데이터가 없으면 false를 반환한다")
        void returnFalseIfNoDataDeleted() {
            // given
            LikeCommand.Unlike command = new LikeCommand.Unlike(1L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L));
            given(likeRepository.deleteBy(command.userId(), command.target())).willReturn(0L);

            // when
            boolean result = likeService.unlike(command);

            // then
            assertThat(result).isFalse();
            verify(likeRepository).deleteBy(command.userId(), command.target());
        }
    }

    @Nested
    @DisplayName("getProductLikes 메서드는")
    class GetProductLikes {

        @Test
        @DisplayName("좋아요한 상품 목록을 반환한다")
        void getProductLikes_returnLikedProducts() {
            // given
            LikeCommand.GetLikeProducts command = new LikeCommand.GetLikeProducts(1L, LikeTarget.TargetType.PRODUCT);
            given(likeRepository.findAllBy(command.userId(), command.targetType())).willReturn(List.of(new com.loopers.domain.like.Like(1L, new LikeTarget(LikeTarget.TargetType.PRODUCT, 1L))));

            // when
            List<LikeInfo> result = likeService.getProductLikes(command);

            // then
            assertThat(result).hasSize(1);
        }
    }
}