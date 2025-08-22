package com.loopers.infrastructure.http;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "payment-gateway",
        url = "${pg.gateway.url}"
)
public interface PgFeignClient {
}
