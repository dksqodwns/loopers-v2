package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return this.orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findByIdAndUserId(Long id, Long userId) {
        return this.orderJpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public List<Order> findAllByUserId(Long userId) {
        return this.orderJpaRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return this.orderJpaRepository.findById(orderId);
    }
}
