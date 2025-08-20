package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public List<BrandInfo> getBrands(final BrandCommand.GetBrands command) {
        return brandRepository.findAllByIds(command.ids()).stream()
                .map(BrandInfo::from)
                .toList();
    }

    public BrandInfo getBrand(final BrandCommand.GetBrand command) {
        return brandRepository.findById(command.id()).map(BrandInfo::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당 브랜드를 찾을 수 없습니다. brandId: " + command.id()));
    }

}
