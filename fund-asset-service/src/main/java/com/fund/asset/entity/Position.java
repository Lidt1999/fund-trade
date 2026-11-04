package com.fund.asset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

@Entity
@Table(name = "position", uniqueConstraints = @UniqueConstraint(name = "uk_position_user_product", columnNames = {"user_id", "product_code"}))
public class Position {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "product_code", nullable = false, length = 32) private String productCode;
    @Column(nullable = false) private Long shares;
    @Column(nullable = false) private Long frozenShares;
    @Version private Long version;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;

    protected Position() { }
    public Position(Long userId, String productCode, Long shares) { this.userId = userId; this.productCode = productCode; this.shares = shares; this.frozenShares = 0L; }
    @jakarta.persistence.PrePersist void prePersist() { createdAt = updatedAt = LocalDateTime.now(); }
    @jakarta.persistence.PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getProductCode() { return productCode; }
    public Long getShares() { return shares; }
    public Long getFrozenShares() { return frozenShares; }
    public void setShares(Long shares) { this.shares = shares; }
    public Long getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
