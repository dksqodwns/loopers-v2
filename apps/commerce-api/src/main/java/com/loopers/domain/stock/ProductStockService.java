package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductStockService {
    private final ProductStockRepository productStockRepository;

    public Optional<ProductStockInfo> findStock(final ProductStockCommand.GetStock command) {
        return this.productStockRepository.findByProductId(command.productId())
                .map(ProductStockInfo::from);
    }

    @Transactional(readOnly = true)
    public List<ProductStockInfo> getStocks(final ProductStockCommand.GetStocks command) {
        return this.productStockRepository.findAllByProductId(command.productIds()).stream()
                .map(ProductStockInfo::from)
                .toList();
    }

    @Transactional
    public void decrease(final ProductStockCommand.Decrease command) {
        ProductStock productStock = productStockRepository.findByProductIdForUpdate(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                        "상품의 재고가 아예 없습니다. productId: " + command.productId()));

        productStock.decrease(command.quantity());
    }
}
