package com.loopers.domain.product;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private static final Duration PRODUCT_DETAIL_TTL = Duration.ofMinutes(10);
    private static final Duration PRODUCT_SEARCH_TTL = Duration.ofMinutes(2);

    private final ProductRepository productRepository;

    private final RedisTemplate<String, String> redis;

    private final ObjectMapper objectMapper;

    public ProductInfo getProduct(ProductCommand.GetProduct command) {
        String key = detailKey(command.id());

        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, ProductInfo.class);
            } catch (Exception ignore) {
            }
        }

        Product product = productRepository.findById(command.id()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "해당하는 상품을 찾을 수 없습니다. ID: " + command.id())
        );
        ProductInfo info = ProductInfo.from(product);

        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(info), PRODUCT_DETAIL_TTL);
        } catch (Exception ignore) {
        }

        return info;
    }

    public Optional<ProductInfo> getProductById(ProductCommand.GetProduct command) {
        return productRepository.findById(command.id())
                .map(ProductInfo::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductInfo> searchProducts(final ProductCommand.SearchProducts command) {
        String key = searchKey(command);

        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            PageCache<ProductInfo> dto = readPageCache(cached);
            if (dto != null) {
                return dto.toPage();
            }
        }

        Page<Product> page = (command.brandId() == null)
                ? productRepository.findAllBy(command.pageRequest())
                : productRepository.findAllBy(command.brandId(), command.pageRequest());

        Page<ProductInfo> mapped = page.map(ProductInfo::from);

        PageCache<ProductInfo> dto = PageCache.from(mapped);
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(dto), PRODUCT_SEARCH_TTL);
        } catch (Exception ignore) {
        }

        return mapped;
    }

    @Transactional(readOnly = true)
    public List<ProductInfo> getProducts(final ProductCommand.GetProducts command) {
        return productRepository.findAllByIds(command.ids()).stream()
                .map(ProductInfo::from)
                .toList();
    }

    public void increaseLikeCount(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "해당하는 상품을 찾을 수 없습니다. ID: " + productId)
        );
        product.like();
    }

    public void decreaseLikeCount(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "해당하는 상품을 찾을 수 없습니다. ID: " + productId)
        );
        product.unlike();

        redis.delete(detailKey(productId));
    }

    private String detailKey(Long id) {
        return "product:detail:" + id;
    }

    private String searchKey(ProductCommand.SearchProducts command) {
        long ver = currentSearchVersion();
        String brand = (command.brandId() == null) ? "all" : String.valueOf(command.brandId());
        String sortSpec = sortSpec(command.pageRequest().getSort());
        int page = command.pageRequest().getPageNumber();
        int size = command.pageRequest().getPageSize();
        return "product:search:v" + ver + ":" + brand + ":p" + page + ":s" + size + ":sort=" + sortSpec;
    }

    private long currentSearchVersion() {
        String v = redis.opsForValue().get("product:search:ver");
        if (v == null) {
            return 1L;
        }
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            return 1L;
        }
    }

    public void bumpSearchVersion() {
        redis.opsForValue().increment("product:search:ver");
    }

    private String sortSpec(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "unsorted";
        }
        StringBuilder sb = new StringBuilder();
        for (Sort.Order o : sort) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(o.getProperty()).append(',').append(o.getDirection().name().toLowerCase());
        }
        return sb.toString();
    }

    private PageCache<ProductInfo> readPageCache(String json) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                    .constructParametricType(PageCache.class, ProductInfo.class);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    private static final class PageCache<T> {
        public long totalElements;
        public int totalPages;
        public int page;
        public int size;
        public List<T> content;

        public PageCache() {
        }

        private PageCache(long totalElements, int totalPages, int page, int size, List<T> content) {
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.page = page;
            this.size = size;
            this.content = content;
        }

        static <T> PageCache<T> from(Page<T> page) {
            return new PageCache<>(
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.getNumber(),
                    page.getSize(),
                    page.getContent()
            );
        }

        Page<T> toPage() {
            return new PageImpl<>(content, PageRequest.of(page, size), totalElements);
        }
    }
}
