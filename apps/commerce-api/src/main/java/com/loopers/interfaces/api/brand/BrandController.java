package com.loopers.interfaces.api.brand;

import com.loopers.domain.brand.BrandCommand.GetBrand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.brand.BrandDto.V1.BrandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brands")
public class BrandController implements BrandV1ApiSpec {

    private final BrandService brandService;

    @Override
    @GetMapping("/{brandId}")
    public ApiResponse<BrandDto.V1.BrandResponse> getBrand(@PathVariable Long brandId) {
        BrandInfo brandInfo = brandService.getBrand(new GetBrand(brandId));
        return ApiResponse.success(BrandResponse.from(brandInfo));
    }

}
