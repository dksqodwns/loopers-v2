package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    private Long userId;

    @Embedded
    private Amount amount;

    public Point(final Long userId, final Amount amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public void charge(final Long chargeAmount) {
        this.amount.charge(chargeAmount);
    }

    public void use(Long amount) {
        this.amount.use(amount);
    }
}
