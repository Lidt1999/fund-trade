package com.fund.trade.client;

import com.fund.common.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "asset-confirmation-service", url = "${asset-service.base-url}")
public interface AssetConfirmationFeignClient {
    @PostMapping("/api/assets/pending/{orderId}/confirm")
    Result<Void> confirmPending(@PathVariable("orderId") String orderId);

    @PostMapping("/api/assets/pending/{orderId}/cancel")
    Result<Void> cancelPending(@PathVariable("orderId") String orderId);
}
