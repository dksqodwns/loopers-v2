package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeTarget {

    @Column(name = "target_id", nullable = false)
    private Long id;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    public LikeTarget(TargetType targetType, Long id) {
        if (id == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "타겟의 ID는 NULL이 될 수 없습니다.");
        }
        if (targetType == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "타겟의 타입은 NULL이 될 수 없습니다.");
        }
        this.targetType = targetType;
        this.id = id;
    }

    public enum TargetType {
        BRAND, PRODUCT;

        public static TargetType from(final String targetType) {
            try {
                return valueOf(targetType.toUpperCase());
            } catch (NullPointerException | IllegalArgumentException e) {
                throw new CoreException(ErrorType.BAD_REQUEST, "좋아요의 대상 타입이 올바르지 않습니다. targetType: " + targetType);
            }
        }
    }
}

