package com.loopers.domain.count;

import java.util.List;
import java.util.Optional;

public interface ProductCountRepository {

    ProductCount save(ProductCount productCount);

    Optional<ProductCount> findByProductId(Long productId);

    List<ProductCount> findAllByProductIds(List<Long> productIds);

}
