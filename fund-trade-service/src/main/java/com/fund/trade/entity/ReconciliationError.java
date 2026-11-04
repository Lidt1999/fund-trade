package com.fund.trade.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_error")
public class ReconciliationError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long reportId;
    @Column(nullable = false, length = 32)
    private String errorType;
    @Column(length = 64)
    private String orderId;
    private Long systemAmount;
    @Column(length = 32)
    private String systemStatus;
    private Long taAmount;
    private Integer taStatus;
    @Column(nullable = false)
    private boolean resolved;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ReconciliationError() {
    }

    public ReconciliationError(Long reportId, String errorType, String orderId,
                               Long systemAmount, String systemStatus,
                               Long taAmount, Integer taStatus) {
        this.reportId = reportId;
        this.errorType = errorType;
        this.orderId = orderId;
        this.systemAmount = systemAmount;
        this.systemStatus = systemStatus;
        this.taAmount = taAmount;
        this.taStatus = taStatus;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getReportId() { return reportId; }
    public String getErrorType() { return errorType; }
    public String getOrderId() { return orderId; }
    public Long getSystemAmount() { return systemAmount; }
    public String getSystemStatus() { return systemStatus; }
    public Long getTaAmount() { return taAmount; }
    public Integer getTaStatus() { return taStatus; }
    public boolean isResolved() { return resolved; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
