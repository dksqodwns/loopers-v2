package com.loopers.domain.like;

public record LikeCommand() {

    public record Like(Long userId, LikeTarget target) {
    }

    public record GetLikeProducts(Long userId, LikeTarget.TargetType targetType) {
    }

    public record Unlike(Long userId, LikeTarget target) {
    }
}
