package com.fund.trade.repository;

import com.fund.trade.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByClientRequestId(String clientRequestId);
    List<PurchaseOrder> findByStatus(String status);
    List<PurchaseOrder> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start, LocalDateTime end);
}
