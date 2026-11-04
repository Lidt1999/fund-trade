package com.fund.asset.service;

import com.fund.asset.entity.PendingAsset;
import com.fund.asset.repository.PendingAssetRepository;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetTccServiceImpl implements AssetTccService {
    private final PendingAssetRepository pendingAssets;

    public AssetTccServiceImpl(PendingAssetRepository pendingAssets) {
        this.pendingAssets = pendingAssets;
    }

    @Override
    @Transactional
    public boolean tryReserve(String orderId, Long userId, String productCode, Long shares) {
        PendingAsset existing = pendingAssets.findByOrderId(orderId).orElse(null);
        if (existing != null) {
            return "TRY".equals(existing.getStatus()) || "PENDING_CONFIRMATION".equals(existing.getStatus());
        }
        pendingAssets.save(new PendingAsset(orderId, userId, productCode, shares, "TRY"));
        return true;
    }

    @Override
    @Transactional
    public boolean confirm(BusinessActionContext context) {
        PendingAsset asset = find(context, "orderId");
        if (asset == null || "CONFIRMED".equals(asset.getStatus())) {
            return asset != null;
        }
        if (!"TRY".equals(asset.getStatus())) {
            return "CANCELLED".equals(asset.getStatus());
        }
        asset.setStatus("PENDING_CONFIRMATION");
        pendingAssets.save(asset);
        return true;
    }

    @Override
    @Transactional
    public boolean cancel(BusinessActionContext context) {
        PendingAsset asset = find(context, "orderId");
        if (asset == null || "CANCELLED".equals(asset.getStatus())) {
            return asset != null;
        }
        if ("PENDING_CONFIRMATION".equals(asset.getStatus())) {
            return true;
        }
        asset.setStatus("CANCELLED");
        pendingAssets.save(asset);
        return true;
    }

    private PendingAsset find(BusinessActionContext context, String key) {
        Object value = context.getActionContext(key);
        return value == null ? null : pendingAssets.findByOrderId(value.toString()).orElse(null);
    }
}
