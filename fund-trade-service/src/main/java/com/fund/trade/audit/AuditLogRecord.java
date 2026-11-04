package com.fund.trade.audit;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLogRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 64)
    private String operation;
    @Column(length = 64)
    private String userId;
    @Column(nullable = false, length = 16)
    private String result;
    @Column(length = 1000)
    private String requestSummary;
    @Column(length = 500)
    private String errorType;
    @Column(nullable = false)
    private long durationMs;
    @Column(nullable = false, length = 200)
    private String requestUri;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected AuditLogRecord() {
    }

    public AuditLogRecord(String operation, String userId, String result,
                          String requestSummary, String errorType, long durationMs,
                          String requestUri) {
        this.operation = operation;
        this.userId = userId;
        this.result = result;
        this.requestSummary = requestSummary;
        this.errorType = errorType;
        this.durationMs = durationMs;
        this.requestUri = requestUri;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getOperation() { return operation; }
    public String getUserId() { return userId; }
    public String getResult() { return result; }
    public String getRequestSummary() { return requestSummary; }
    public String getErrorType() { return errorType; }
    public long getDurationMs() { return durationMs; }
    public String getRequestUri() { return requestUri; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
