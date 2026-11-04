package com.fund.trade.message;

public record OrderMessage(String orderId, Long userId, String productCode,
                           Long amount, Integer orderType, Long timestamp) { }
