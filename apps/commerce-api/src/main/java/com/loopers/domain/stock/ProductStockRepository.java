package com.loopers.domain.stock;

import java.util.List;
import java.util.Optional;

public interface ProductStockRepository {
    Optional<ProductStock> findByProductId(Long productId);

    List<ProductStock> findAllByProductId(List<Long> productIds);

    Optional<ProductStock> findByProductIdForUpdate(Long productId);

}
