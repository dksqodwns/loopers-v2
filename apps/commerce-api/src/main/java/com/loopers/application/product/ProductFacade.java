package com.loopers.application.product;

import com.loopers.application.product.ProductCriteria.SearchProducts;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandCommand.GetBrands;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.count.ProductCountCommand.GetProductCounts;
import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.ProductStockCommand.GetStocks;
import com.loopers.domain.stock.ProductStockInfo;
import com.loopers.domain.stock.ProductStockService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductFacade {
    private final ProductService productService;
    private final ProductStockService productStockService;
    private final ProductCountService productCountService;
    private final BrandService brandService;

    public ProductResult getProduct(final ProductCriteria.GetProduct criteria) {
        final ProductInfo productInfo = this.productService.getProduct(criteria.toProductCommand());
        final BrandInfo brandInfo = brandService.getBrand(new BrandCommand.GetBrand(productInfo.getBrandId()));
        final Optional<ProductStockInfo> stockInfo = productStockService.findStock(criteria.toStockCommand());
        final Optional<ProductCountInfo> countInfo = productCountService.getProductCount(criteria.toCountCommand());

        return ProductResult.of(productInfo, brandInfo, stockInfo, countInfo);
    }

    public ProductResult.SearchProducts searchProducts(final SearchProducts criteria) {
        final Page<ProductInfo> productInfos = productService.searchProducts(criteria.toCommand());

        final List<Long> brandIds = productInfos.getContent().stream()
                .map(ProductInfo::getBrandId)
                .distinct()
                .toList();
        List<BrandInfo> brandInfos = brandService.getBrands(new GetBrands(brandIds));
        List<Long> productIds = productInfos.getContent().stream()
                .map(ProductInfo::id)
                .toList();
        List<ProductStockInfo> stockInfos = productStockService.getStocks(new GetStocks(productIds));
        List<ProductCountInfo> countInfos = productCountService.getProductCounts(new GetProductCounts(productIds));

        return ProductResult.SearchProducts.of(productInfos, brandInfos, stockInfos, countInfos);
    }

}
