package com.fund.trade.client;

import com.fund.common.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "asset-service", contextId = "assetServiceFeignClient", url = "${asset-service.base-url}")
public interface AssetServiceFeignClient {
    @PostMapping("/api/assets/pending")
    Result<Void> createPendingAsset(@RequestBody PendingAssetCommand command);

    @PostMapping("/api/assets/positions/freeze")
    Result<Void> freezePositionShares(@RequestBody FreezePositionCommand command);
}
