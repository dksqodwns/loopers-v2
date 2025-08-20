package com.loopers.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    List<Order> findAllByUserId(Long userId);

    Optional<Order> findById(Long orderId);

}
