package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order(final Long userId, final List<OrderItem> items) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유저 ID는 비어있을 수 없습니다.");
        }

        if (items == null || items.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상품은 비어있을 수 없습니다.");
        }

        this.userId = userId;
        this.status = OrderStatus.PENDING;
        items.forEach(this::addOrderItem);
    }

    public void addOrderItem(final OrderItem item) {
        this.items.add(item);
        item.initOrder(this);
    }

    public Long calculateTotalPrice() {
        return this.items.stream()
                .map(OrderItem::calculateTotalPrice)
                .reduce(0L, Long::sum);
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    public void confirm() {
        this.status = OrderStatus.CONFIRM;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }
}
