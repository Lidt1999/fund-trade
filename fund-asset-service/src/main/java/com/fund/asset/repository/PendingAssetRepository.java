package com.fund.asset.repository;

import com.fund.asset.entity.PendingAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PendingAssetRepository extends JpaRepository<PendingAsset, Long> {
    Optional<PendingAsset> findByOrderId(String orderId);
}
