package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Amount {
    private Long amount;

    public Amount(final Long amount) {
        if (amount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0원 이상이어야 합니다.");
        }

        this.amount = amount;
    }

    public void charge(final Long amount) {
        if (amount < 100) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 포인트는 100원 이상이어야 합니다.");
        }
        this.amount += amount;
    }

    public void use(final Long amount) {
        if (this.amount < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, "보유한 포인트가 부족합니다.");
        }
        this.amount -= amount;
    }
}
