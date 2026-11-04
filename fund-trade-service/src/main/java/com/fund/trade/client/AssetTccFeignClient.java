package com.fund.trade.client;

import com.fund.common.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "asset-service", contextId = "assetTccFeignClient", url = "${asset-service.base-url}")
public interface AssetTccFeignClient {
    @PostMapping("/api/assets/pending/tcc/try")
    Result<Boolean> tryReserve(@RequestBody AssetTccCommand command);
}
