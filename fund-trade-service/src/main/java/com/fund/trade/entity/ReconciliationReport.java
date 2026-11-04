package com.fund.trade.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_report", uniqueConstraints = {
        @UniqueConstraint(name = "uk_reconciliation_report_date", columnNames = "report_date")
})
public class ReconciliationReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    @Column(nullable = false)
    private int totalOrders;
    @Column(nullable = false)
    private int matchedCount;
    @Column(nullable = false)
    private int mismatchCount;
    @Column(nullable = false)
    private int missingInTa;
    @Column(nullable = false)
    private int missingInSystem;
    @Column(nullable = false)
    private int status;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ReconciliationReport() {
    }

    public ReconciliationReport(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LocalDate getReportDate() { return reportDate; }
    public int getTotalOrders() { return totalOrders; }
    public int getMatchedCount() { return matchedCount; }
    public int getMismatchCount() { return mismatchCount; }
    public int getMissingInTa() { return missingInTa; }
    public int getMissingInSystem() { return missingInSystem; }
    public int getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    public void setMatchedCount(int matchedCount) { this.matchedCount = matchedCount; }
    public void setMismatchCount(int mismatchCount) { this.mismatchCount = mismatchCount; }
    public void setMissingInTa(int missingInTa) { this.missingInTa = missingInTa; }
    public void setMissingInSystem(int missingInSystem) { this.missingInSystem = missingInSystem; }
    public void setStatus(int status) { this.status = status; }
}
