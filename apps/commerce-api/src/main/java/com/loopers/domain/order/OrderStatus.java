package com.loopers.domain.order;


public enum OrderStatus {
    // 주문 접수
    PENDING,

    // 결제 대기
    CONFIRM,

    // 결제 취소
    CANCELED,

    // 결제 완료
    COMPLETED
}
