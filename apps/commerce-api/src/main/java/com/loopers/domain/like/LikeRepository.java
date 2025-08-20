package com.loopers.domain.like;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    boolean saveIfAbsent(Like like);

    long deleteBy(Long userId, LikeTarget likeTarget);

    boolean existsBy(Long userId, LikeTarget likeTarget);

    void save(Like like);

    void delete(Like like);

    Optional<Like> findBy(Long userId, LikeTarget likeTarget);


    List<Like> findAllBy(Long userId, LikeTarget.TargetType targetType);
}
