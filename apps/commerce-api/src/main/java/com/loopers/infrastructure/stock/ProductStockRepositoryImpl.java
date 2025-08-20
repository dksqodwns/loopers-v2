package com.loopers.infrastructure.stock;

import com.loopers.domain.stock.ProductStock;
import com.loopers.domain.stock.ProductStockRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductStockRepositoryImpl implements ProductStockRepository {
    private final ProductStockJpaRepository productStockJpaRepository;

    @Override
    public Optional<ProductStock> findByProductId(Long productId) {
        return this.productStockJpaRepository.findByProductId(productId);
    }

    @Override
    public List<ProductStock> findAllByProductId(List<Long> productIds) {
        return this.productStockJpaRepository.findAllByProductIdIn(productIds);
    }

    @Override
    public Optional<ProductStock> findByProductIdForUpdate(Long productId) {
        return this.productStockJpaRepository.findByProductIdForUpdate(productId);
    }
}
