package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "target_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_like_user_target",
                columnNames = {"ref_user_id", "target_id", "target_type"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Embedded
    private LikeTarget likeTarget;

    public Like(final Long userId, final LikeTarget likeTarget) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유저 ID는 NULL이 될 수 없습니다.");
        }
        if (likeTarget == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "좋아요 대상은 NULL이 될 수 없습니다.");
        }

        this.userId = userId;
        this.likeTarget = likeTarget;
    }
}
