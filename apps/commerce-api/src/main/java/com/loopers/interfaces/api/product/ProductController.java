package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import com.loopers.application.product.ProductCriteria.GetProduct;
import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.application.product.ProductResult.SearchProducts;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductDto.V1.ProductResponse;
import com.loopers.interfaces.api.product.ProductDto.V1.SearchProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductDto.V1.ProductResponse> getProduct(@PathVariable final Long productId
    ) {
        ProductResult productResult = productFacade.getProduct(new GetProduct(productId));
        ProductResponse response = ProductResponse.from(productResult);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping
    public ApiResponse<SearchProductResponse> searchProducts(
            @RequestParam(required = false) final Long brandId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        SearchProducts searchProducts = productFacade.searchProducts(ProductCriteria.SearchProducts.of(
                brandId, pageable
        ));

        return ApiResponse.success(SearchProductResponse.from(searchProducts));
    }
}
