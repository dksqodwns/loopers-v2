package com.loopers.domain.like;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    
    @Transactional
    public boolean like(final LikeCommand.Like command) {
        final Like like = new Like(command.userId(), command.target());
        return likeRepository.saveIfAbsent(like);
    }

    @Transactional
    public boolean unlike(final LikeCommand.Unlike command) {
        long removed = likeRepository.deleteBy(command.userId(), command.target());
        return removed > 0;
    }

    public List<LikeInfo> getProductLikes(final LikeCommand.GetLikeProducts command) {
        return likeRepository.findAllBy(command.userId(), command.targetType()).stream()
                .map(LikeInfo::from)
                .toList();
    }

}
