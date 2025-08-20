package com.loopers.domain.order.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderPlacedEvent {

    private final Long orderId;
    private final Long userId;
    private final Long totalPrice;
}
