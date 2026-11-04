package com.fund.trade.client;

public record PendingAssetCommand(String orderId, Long userId, String productCode, Long shares, String status) {
}
