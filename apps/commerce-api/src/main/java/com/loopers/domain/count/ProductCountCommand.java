package com.loopers.domain.count;

import com.loopers.domain.count.ProductCount.CountType;
import java.util.List;

public record ProductCountCommand() {

    public record Increase(Long productId, CountType countType) {
    }

    public record Decrease(Long productId, CountType countType) {
    }

    public record GetProductCount(Long productId) {
    }

    public record GetProductCounts(List<Long> productIds) {
    }
}
