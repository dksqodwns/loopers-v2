package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.like.LikeTarget;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LikeRepositoryImpl implements LikeRepository {
    private final LikeJpaRepository likeJpaRepository;

    @Override
    public void save(Like like) {
        this.likeJpaRepository.save(like);
    }

    @Override
    public boolean existsBy(Long userId, LikeTarget likeTarget) {
        return this.likeJpaRepository.existsByUserIdAndLikeTarget(userId, likeTarget);
    }

    @Override
    public void delete(Like like) {
        this.likeJpaRepository.delete(like);
    }

    @Override
    public Optional<Like> findBy(Long userId, LikeTarget likeTarget) {
        return this.likeJpaRepository.findByUserIdAndLikeTarget(userId, likeTarget);
    }

    @Override
    public List<Like> findAllBy(Long userId, LikeTarget.TargetType targetType) {
        return this.likeJpaRepository.findAllByUserIdAndLikeTarget_TargetType(userId, targetType);
    }

    @Override
    public boolean saveIfAbsent(Like like) {
        try {
            this.likeJpaRepository.saveAndFlush(like);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long deleteBy(Long userId, LikeTarget likeTarget) {
        return this.likeJpaRepository.deleteByUserIdAndLikeTarget(userId, likeTarget);
    }

}
