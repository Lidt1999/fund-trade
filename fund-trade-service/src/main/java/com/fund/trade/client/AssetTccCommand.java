package com.fund.trade.client;

public record AssetTccCommand(String orderId, Long userId, String productCode, Long shares, String status) { }
