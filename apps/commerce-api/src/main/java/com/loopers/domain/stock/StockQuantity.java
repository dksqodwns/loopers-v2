package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockQuantity {

    @Column(name = "stock", nullable = false)
    private Integer stock;

    public StockQuantity(Integer stock) {
        if (stock == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품의 재고는 비어있을 수 없습니다.");
        }
        if (stock < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품의 재고는 음수가 될 수 없습니다.");
        }

        this.stock = stock;
    }

    public void decrease(final Integer quantity) {
        if (this.stock < quantity) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품의 재고가 차감 수량보다 부족합니다.");
        }

        this.stock -= quantity;
    }
}
