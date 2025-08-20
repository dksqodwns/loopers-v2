package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_order_id")
    private Order order;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, precision = 10, scale = 2)
    private Long price;

    @Column(nullable = false)
    private Integer quantity;

    public OrderItem(final Long productId, final Long price, final Integer quantity) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 비어있을 수 없습니다.");
        }
        if (quantity == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상품 수량은 비어있을 수 없습니다.");
        }
        if (quantity < 1) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상품 수량은 최소 1개 이상이어야 합니다.");
        }
        if (price == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 비어있을 수 없습니다.");
        }
        if (price < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 음수가 될 수 없습니다.");
        }

        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public Long calculateTotalPrice() {
        return price * quantity;
    }

    void initOrder(final Order order) {
        if (this.order == null) {
            this.order = order;
        }
    }
}
