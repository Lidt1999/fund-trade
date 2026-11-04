package com.fund.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String orderId;
    @Column(nullable = false, unique = true, length = 64)
    private String clientRequestId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, length = 32)
    private String productCode;
    @Column(nullable = false)
    private Long shares;
    @Column(nullable = false, length = 32)
    private String status;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected PurchaseOrder() { }

    public PurchaseOrder(String orderId, String clientRequestId, Long userId, String productCode, Long shares) {
        this.orderId = orderId;
        this.clientRequestId = clientRequestId;
        this.userId = userId;
        this.productCode = productCode;
        this.shares = shares;
        this.status = "PENDING_CONFIRMATION";
    }

    @jakarta.persistence.PrePersist
    void prePersist() { createdAt = updatedAt = LocalDateTime.now(); }

    @jakarta.persistence.PreUpdate
    void preUpdate() { updatedAt = LocalDateTime.now(); }

    public String getOrderId() { return orderId; }
    public String getClientRequestId() { return clientRequestId; }
    public Long getUserId() { return userId; }
    public String getProductCode() { return productCode; }
    public Long getShares() { return shares; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
