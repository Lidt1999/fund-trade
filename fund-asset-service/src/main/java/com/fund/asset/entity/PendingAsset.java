package com.fund.asset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_asset")
public class PendingAsset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 64) private String orderId;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, length = 32) private String productCode;
    @Column(nullable = false) private Long shares;
    @Column(nullable = false, length = 16) private String status;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;

    protected PendingAsset() { }
    public PendingAsset(String orderId, Long userId, String productCode, Long shares, String status) { this.orderId = orderId; this.userId = userId; this.productCode = productCode; this.shares = shares; this.status = status; }
    @jakarta.persistence.PrePersist void prePersist() { createdAt = updatedAt = LocalDateTime.now(); }
    @jakarta.persistence.PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public String getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public String getProductCode() { return productCode; }
    public Long getShares() { return shares; }
    public String getStatus() { return status; }
    public void setShares(Long shares) { this.shares = shares; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
