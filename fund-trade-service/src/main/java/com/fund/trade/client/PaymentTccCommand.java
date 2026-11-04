package com.fund.trade.client;

public record PaymentTccCommand(String requestId, String orderId, Long userId, Long amount) { }
