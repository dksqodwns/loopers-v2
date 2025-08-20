package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointCriteria;
import com.loopers.application.point.PointResult;

public record PointDto() {

    public record V1() {

        public record ChargeRequest(Long amount) {
            public PointCriteria.Charge toCriteira(Long userId) {
                return new PointCriteria.Charge(userId, amount);
            }
        }

        public record PointResponse(Long id, Long userId, Long amount) {
            public static PointResponse from(final PointResult pointResult) {
                return new PointResponse(pointResult.id(), pointResult.userId(), pointResult.amount());
            }
        }

    }

}
