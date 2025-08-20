package com.loopers.domain.like;

public record LikeInfo(Long userId, LikeTarget likeTarget) {

    public static LikeInfo from(Like like) {
        return new LikeInfo(like.getUserId(), like.getLikeTarget());
    }

    public Long getTargetId() {
        return likeTarget.getId();
    }


}
