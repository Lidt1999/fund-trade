package com.fund.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "request_id", nullable = false, unique = true, length = 64) private String requestId;
    @Column(name = "order_id", nullable = false, length = 64) private String orderId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(nullable = false) private Long amount;
    @Column(nullable = false, length = 16) private String operation;
    @Column(nullable = false, length = 16) private String status;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;

    protected PaymentTransaction() {}
    public PaymentTransaction(String requestId, String orderId, Long userId, Long amount, String operation) {
        this.requestId = requestId; this.orderId = orderId; this.userId = userId;
        this.amount = amount; this.operation = operation; this.status = "SUCCESS";
    }
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    public String getRequestId() { return requestId; }
    public String getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public Long getAmount() { return amount; }
    public String getOperation() { return operation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
