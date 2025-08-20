package com.loopers.domain.count;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_count")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCount extends BaseEntity {

    @Column(name = "ref_product_id")
    private Long productId;

    @AttributeOverride(name = "count", column = @Column(name = "like_count"))
    @Embedded
    private Count likeCount;

    public ProductCount(Long productId) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 비어있을 수 없습니다.");
        }

        this.productId = productId;
        this.likeCount = new Count(0L);
    }

    public void increase(final CountType countType) {
        switch (countType) {
            case LIKE -> likeCount.increase();
        }
    }

    public void decrease(final CountType countType) {
        switch (countType) {
            case LIKE -> likeCount.decrease();
        }
    }

    @Getter
    @EqualsAndHashCode
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Count {

        private Long count;

        public Count(Long count) {
            if (count == null) {
                throw new CoreException(ErrorType.BAD_REQUEST, "카운트는 NULL이 될 수 없습니다.");
            }

            if (count < 0) {
                throw new CoreException(ErrorType.BAD_REQUEST, "카운트는 음수가 될 수 없습니다.");
            }

            this.count = count;
        }

        public void increase() {
            this.count++;
        }

        public void decrease() {
            if (this.count <= 0) {
                throw new CoreException(ErrorType.BAD_REQUEST, "카운트를 감소 시킬 수 없습니다.");
            }
            this.count--;
        }
    }

    public enum CountType {
        LIKE,
    }
}


