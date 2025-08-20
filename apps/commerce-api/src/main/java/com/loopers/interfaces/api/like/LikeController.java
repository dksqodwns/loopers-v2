package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeCriteria;
import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeResult.GetLikeProducts;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.like.LikeDto.V1.GetLikeProductsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/like")
public class LikeController implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @Override
    @GetMapping("/products")
    public ApiResponse<GetLikeProductsResponse> getLikeProducts(@RequestHeader("X-USER-ID") final Long userId) {
        GetLikeProducts likeProducts = likeFacade.getLikeProducts(new LikeCriteria.GetLikeProducts(userId));
        return ApiResponse.success(GetLikeProductsResponse.from(likeProducts));
    }

    @Override
    @DeleteMapping("/products/{productId}")
    public ApiResponse<Object> unlike(@RequestHeader("X-USER-ID") final Long userId, @PathVariable final Long productId) {
        likeFacade.like(LikeCriteria.Like.likeProduct(userId, productId));
        return ApiResponse.success();
    }

    @Override
    @PostMapping("/products/{productId}")
    public ApiResponse<Object> like(@RequestHeader("X-USER-ID") final Long userId, @PathVariable final Long productId) {
        likeFacade.unlike(LikeCriteria.Unlike.unlikeProduct(userId, productId));
        return ApiResponse.success();
    }
}
