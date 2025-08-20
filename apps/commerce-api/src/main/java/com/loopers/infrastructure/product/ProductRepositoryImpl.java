package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(Long id) {
        return this.productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findAllByIds(List<Long> ids) {
        return this.productJpaRepository.findAllById(ids);
    }

    @Override
    public Page<Product> findAllBy(Pageable pageable) {
        return this.productJpaRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findAllBy(Long brandId, Pageable pageable) {
        return this.productJpaRepository.findAllByBrandId_BrandId(brandId, pageable);
    }
}
