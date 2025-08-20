package com.loopers.domain.product;

import java.util.List;
import org.springframework.data.domain.PageRequest;

public record ProductCommand() {
    public record GetProduct(Long id) {
    }

    public record GetProducts(List<Long> ids) {
    }

    public record SearchProducts(Long brandId, PageRequest pageRequest) {
    }

}
