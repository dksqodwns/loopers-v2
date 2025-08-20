package com.loopers.domain.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {
    Optional<Product> findById(Long id);

    List<Product> findAllByIds(List<Long> ids);

    Page<Product> findAllBy(Long brandId, Pageable pageable);

    Page<Product> findAllBy(Pageable pageable);

}
