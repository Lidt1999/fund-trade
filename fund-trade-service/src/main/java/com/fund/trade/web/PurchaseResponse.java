package com.fund.trade.web;

import com.fund.trade.entity.PurchaseOrder;

import java.time.LocalDateTime;

public record PurchaseResponse(String orderId, String clientRequestId, Long userId, String productCode,
                               Long shares, String status, LocalDateTime createdAt) {
    public static PurchaseResponse from(PurchaseOrder order) {
        return new PurchaseResponse(order.getOrderId(), order.getClientRequestId(), order.getUserId(),
                order.getProductCode(), order.getShares(), order.getStatus(), order.getCreatedAt());
    }
}
