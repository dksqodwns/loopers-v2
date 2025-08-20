package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeResult;
import java.util.List;

public record LikeDto() {
    public record V1() {

        public record GetLikeProductsResponse(List<GetLikeProductResponse> likeProducts) {
            public static GetLikeProductsResponse from(final LikeResult.GetLikeProducts likeResult) {
                List<GetLikeProductResponse> likeProducts = likeResult.products().stream()
                        .map(GetLikeProductResponse::from)
                        .toList();

                return new GetLikeProductsResponse(likeProducts);
            }
        }

        public record GetLikeProductResponse(Long id, String name, Long price) {
            public static GetLikeProductResponse from(final LikeResult.GetLikeProduct likeResult) {
                return new GetLikeProductResponse(likeResult.id(), likeResult.name(), likeResult.price());
            }
        }
    }
}
