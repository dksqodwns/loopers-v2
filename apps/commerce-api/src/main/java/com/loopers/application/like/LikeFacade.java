package com.loopers.application.like;

import com.loopers.application.like.LikeResult.GetLikeProducts;
import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductCommand.GetProducts;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeFacade {

    private final LikeService likeService;
    private final ProductService productService;
    private final ProductCountService productCountService;

    @Transactional
    public void like(final LikeCriteria.Like criteria) {
        Optional<ProductInfo> productInfo = productService.getProductById(criteria.toProductCommand());
        if (productInfo.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND,
                    "좋아요를 등록 할 상품을 찾을 수 없습니다. productId: " + criteria.toProductCommand().id());
        }

        boolean liked = likeService.like(criteria.toLikeCommand());

        if (liked) {
            productCountService.increse(criteria.toCountCommand());
            productService.increaseLikeCount(criteria.toProductCommand().id());
        }
    }

    @Transactional
    public void unlike(final LikeCriteria.Unlike criteria) {
        Optional<ProductInfo> productInfo = productService.getProductById(criteria.toProductCommand());
        if (productInfo.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND,
                    "좋아요를 취소 할 상품을 찾을 수 없습니다. productId: " + criteria.toProductCommand().id());
        }
        boolean unliked = likeService.unlike(criteria.toLikeCommand());
        if (unliked) {
            productCountService.decrese(criteria.toCountCommand());
            productService.decreaseLikeCount(criteria.toProductCommand().id());
        }
    }

    public GetLikeProducts getLikeProducts(final LikeCriteria.GetLikeProducts criteria) {
        List<LikeInfo> likeInfos = likeService.getProductLikes(criteria.toCommand());
        List<Long> productIds = likeInfos.stream()
                .map(LikeInfo::getTargetId)
                .toList();
        List<ProductInfo> productInfos = productService.getProducts(new GetProducts(productIds));
        return GetLikeProducts.from(productInfos);
    }

}
