package com.loopers.application.order;

import com.loopers.domain.payment.CardCompany;

public record CardCriteria() {

    public record Order(CardCompany cardCompany, String cardNo) {

    }

}
