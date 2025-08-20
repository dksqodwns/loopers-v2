package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return this.pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(Long uid) {
        return pointJpaRepository.findByUserIdForUpdate(uid);
    }

    @Override
    public Point save(Point point) {
        return this.pointJpaRepository.save(point);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return this.pointJpaRepository.existsByUserId(userId);
    }
}
