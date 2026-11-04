package com.fund.trade.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ta_confirm_record")
public class TaConfirmRecord {
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 32)
    private String fileId;
    @Column(nullable = false, unique = true, length = 64)
    private String orderId;
    @Column(nullable = false)
    private Integer confirmStatus;
    @Column(nullable = false)
    private Long confirmedAmount;
    @Column(length = 200)
    private String failReason;
    @Column(nullable = false)
    private LocalDate confirmDate;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected TaConfirmRecord() { }

    public TaConfirmRecord(String fileId, String orderId, int confirmStatus,
                           Long confirmedAmount, String failReason, LocalDate confirmDate) {
        this.fileId = fileId;
        this.orderId = orderId;
        this.confirmStatus = confirmStatus;
        this.confirmedAmount = confirmedAmount;
        this.failReason = failReason;
        this.confirmDate = confirmDate;
    }

    @PrePersist
    void prePersist() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getFileId() { return fileId; }
    public String getOrderId() { return orderId; }
    public Integer getConfirmStatus() { return confirmStatus; }
    public Long getConfirmedAmount() { return confirmedAmount; }
    public String getFailReason() { return failReason; }
    public LocalDate getConfirmDate() { return confirmDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
