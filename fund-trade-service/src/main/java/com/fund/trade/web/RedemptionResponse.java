package com.fund.trade.web;

import com.fund.trade.entity.RedemptionOrder;

import java.time.LocalDateTime;

public record RedemptionResponse(String orderId, String clientRequestId, Long userId, String productCode,
                                 Long shares, String status, LocalDateTime createdAt) {
    public static RedemptionResponse from(RedemptionOrder order) {
        return new RedemptionResponse(order.getOrderId(), order.getClientRequestId(), order.getUserId(),
                order.getProductCode(), order.getShares(), order.getStatus(), order.getCreatedAt());
    }
}
