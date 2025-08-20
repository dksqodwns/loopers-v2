package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeTarget;
import com.loopers.domain.like.LikeTarget.TargetType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndLikeTarget(Long userId, LikeTarget likeTarget);

    Optional<Like> findByUserIdAndLikeTarget(Long userId, LikeTarget likeTarget);

    List<Like> findAllByUserIdAndLikeTarget_TargetType(Long userId, TargetType targetType);

    long deleteByUserIdAndLikeTarget(Long userId, LikeTarget likeTarget);
}
