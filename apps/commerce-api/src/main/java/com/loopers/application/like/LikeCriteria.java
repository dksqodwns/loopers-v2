package com.loopers.application.like;


import com.loopers.domain.count.ProductCount.CountType;
import com.loopers.domain.count.ProductCountCommand;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeTarget;
import com.loopers.domain.like.LikeTarget.TargetType;
import com.loopers.domain.product.ProductCommand;

public record LikeCriteria() {
    public record Like(Long userId, Long productId, TargetType targetType) {

        public static Like likeProduct(final Long userId, final Long productId) {
            return new Like(userId, productId, TargetType.PRODUCT);
        }


        public LikeCommand.Like toLikeCommand() {
            return new LikeCommand.Like(userId, new LikeTarget(TargetType.PRODUCT, productId));
        }

        public ProductCommand.GetProduct toProductCommand() {
            return new ProductCommand.GetProduct(productId);
        }

        public ProductCountCommand.Increase toCountCommand() {
            return new ProductCountCommand.Increase(productId, CountType.LIKE);
        }
    }


    public record GetLikeProducts(Long userId) {

        public LikeCommand.GetLikeProducts toCommand() {
            return new LikeCommand.GetLikeProducts(userId, TargetType.PRODUCT);
        }
    }

    public record Unlike(Long userId, Long productId, TargetType targetType) {
        public static Unlike unlikeProduct(final Long userId, final Long productId) {
            return new Unlike(userId, productId, TargetType.PRODUCT);
        }

        public LikeCommand.Unlike toLikeCommand() {
            return new LikeCommand.Unlike(userId, new LikeTarget(TargetType.PRODUCT, productId));
        }

        public ProductCommand.GetProduct toProductCommand() {
            return new ProductCommand.GetProduct(productId);
        }

        public ProductCountCommand.Decrease toCountCommand() {
            return new ProductCountCommand.Decrease(productId, CountType.LIKE);
        }
    }
}
