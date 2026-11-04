package com.fund.trade.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "local_message")
public class LocalMessage {
    public static final int PENDING = 0;
    public static final int SENT = 1;
    public static final int FAILED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 32)
    private String messageId;
    @Column(nullable = false, length = 64)
    private String topic;
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private Integer status;
    @Column(nullable = false)
    private Integer retryCount;
    @Column(nullable = false)
    private Integer maxRetry;
    @Column(nullable = false)
    private LocalDateTime nextRetryTime;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected LocalMessage() { }

    public LocalMessage(String messageId, String topic, String content, int maxRetry) {
        this.messageId = messageId;
        this.topic = topic;
        this.content = content;
        this.status = PENDING;
        this.retryCount = 0;
        this.maxRetry = maxRetry;
        this.nextRetryTime = LocalDateTime.now();
    }

    @PrePersist
    void prePersist() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    void preUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getMessageId() { return messageId; }
    public String getTopic() { return topic; }
    public String getContent() { return content; }
    public Integer getStatus() { return status; }
    public Integer getRetryCount() { return retryCount; }
    public Integer getMaxRetry() { return maxRetry; }
    public LocalDateTime getNextRetryTime() { return nextRetryTime; }

    public void markSent() { status = SENT; }

    public boolean canRetry() {
        return retryCount < maxRetry;
    }

    public void markFailed() {
        status = FAILED;
        retryCount++;
        nextRetryTime = LocalDateTime.now().plusMinutes(5L * retryCount);
    }
}
