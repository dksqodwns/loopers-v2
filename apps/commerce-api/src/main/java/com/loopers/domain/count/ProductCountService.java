package com.loopers.domain.count;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductCountService {

    private final ProductCountRepository productCountRepository;

    @Transactional
    public void increse(final ProductCountCommand.Increase command) {
        final ProductCount productCount = productCountRepository.findByProductId(command.productId())
                .orElseGet(() -> new ProductCount(command.productId()));

        productCount.increase(command.countType());
        productCountRepository.save(productCount);
    }

    @Transactional
    public void decrese(final ProductCountCommand.Decrease command) {
        productCountRepository.findByProductId(command.productId())
                .ifPresent(productCount -> productCount.decrease(command.countType()));
    }

    public List<ProductCountInfo> getProductCounts(final ProductCountCommand.GetProductCounts command) {
        return productCountRepository.findAllByProductIds(command.productIds()).stream()
                .map(ProductCountInfo::from)
                .toList();
    }

    public Optional<ProductCountInfo> getProductCount(final ProductCountCommand.GetProductCount command) {
        return productCountRepository.findByProductId(command.productId())
                .map(ProductCountInfo::from);
    }

}
