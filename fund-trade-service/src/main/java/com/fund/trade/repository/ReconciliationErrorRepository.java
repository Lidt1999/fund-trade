package com.fund.trade.repository;

import com.fund.trade.entity.ReconciliationError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReconciliationErrorRepository extends JpaRepository<ReconciliationError, Long> {
    List<ReconciliationError> findByReportId(Long reportId);
}
