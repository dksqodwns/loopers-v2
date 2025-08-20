package com.loopers.domain.stock;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "product_stock",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_stock_product_id",
                columnNames = "product_id"
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStock extends BaseEntity {
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Embedded
    private StockQuantity stock;

    public ProductStock(Long productId, StockQuantity stock) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 Id는 비어있을 수 없습니다.");
        }
        if (stock == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고 수량은 비어있을 수 없습니다.");
        }
        this.productId = productId;
        this.stock = stock;
    }

    public void decrease(final Integer quantity) { this.stock.decrease(quantity); }
}
