package com.loopers.domain.stock;

import java.util.List;

public record ProductStockCommand() {
    public record GetStock(Long productId) {
    }

    public record GetStocks(List<Long> productIds) {
    }

    public record Decrease(Long productId, Integer quantity) {
    }

}
