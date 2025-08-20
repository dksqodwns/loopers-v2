package com.loopers.domain.stock;

public record ProductStockInfo(Long productId, StockQuantity stock) {

    public static ProductStockInfo from(ProductStock productStock) {
        return new ProductStockInfo(productStock.getProductId(), productStock.getStock());
    }

    public Integer getStock() {
        return stock.getStock();
    }
}
