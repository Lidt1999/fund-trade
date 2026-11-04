package com.fund.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_account")
public class PaymentAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false, unique = true) private Long userId;
    @Column(nullable = false) private Long availableBalance;
    @Column(nullable = false) private Long frozenBalance;
    @Version private Long version;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;

    protected PaymentAccount() {}
    public PaymentAccount(Long userId, Long availableBalance) {
        this.userId = userId; this.availableBalance = availableBalance; this.frozenBalance = 0L;
    }
    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getAvailableBalance() { return availableBalance; }
    public Long getFrozenBalance() { return frozenBalance; }
    public void setAvailableBalance(Long value) { availableBalance = value; }
    public void setFrozenBalance(Long value) { frozenBalance = value; }
    public Long getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
