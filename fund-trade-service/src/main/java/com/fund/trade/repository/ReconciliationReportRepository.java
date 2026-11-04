package com.fund.trade.repository;

import com.fund.trade.entity.ReconciliationReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ReconciliationReportRepository extends JpaRepository<ReconciliationReport, Long> {
    Optional<ReconciliationReport> findByReportDate(LocalDate reportDate);
}
