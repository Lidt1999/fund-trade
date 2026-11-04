package com.fund.trade.client;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AssetServiceClient {
    private final AssetServiceFeignClient client;

    public AssetServiceClient(AssetServiceFeignClient client) {
        this.client = client;
    }

    public void createPendingAsset(String orderId, Long userId, String productCode, Long shares) {
        client.createPendingAsset(new PendingAssetCommand(
                orderId, userId, productCode, shares, "PENDING_CONFIRMATION"));
    }

    public void freezePositionShares(Long userId, String productCode, Long shares) {
        try {
            client.freezePositionShares(new FreezePositionCommand(userId, productCode, shares));
        } catch (FeignException exception) {
            if (exception.status() >= 400 && exception.status() < 500) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient redeemable shares");
            }
            throw exception;
        }
    }
}
