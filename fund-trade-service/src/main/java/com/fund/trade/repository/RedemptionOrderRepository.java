package com.fund.trade.repository;

import com.fund.trade.entity.RedemptionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface RedemptionOrderRepository extends JpaRepository<RedemptionOrder, Long> {
    Optional<RedemptionOrder> findByClientRequestId(String clientRequestId);
    List<RedemptionOrder> findByStatus(String status);
    List<RedemptionOrder> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start, LocalDateTime end);
}
