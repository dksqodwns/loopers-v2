package com.loopers.domain.payment;


public enum PaymentStatus {
    REQUESTED, // 결제 요청
    COMPLETED, // 결제 완료
    FAILED; // 결제 실패

    public static PaymentStatus from(String pgStatus) {
        if ("SUCCESS".equalsIgnoreCase(pgStatus)) {
            return PaymentStatus.COMPLETED;
        }

        return FAILED;
    }
}
