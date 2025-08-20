package com.loopers.domain.point;

import java.util.Optional;

public interface PointRepository {
    Point save(Point point);

    Optional<Point> findByUserIdForUpdate(Long userId);

    Optional<Point> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
