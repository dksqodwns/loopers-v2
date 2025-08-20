package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;

public record PointResult(
        Long id, Long userId, Long amount
) {
    public static PointResult from(final PointInfo pointInfo) {
        return new PointResult(
                pointInfo.id(),
                pointInfo.userId(),
                pointInfo.getAmount()
        );
    }
}
