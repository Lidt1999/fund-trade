package com.fund.trade.client;

public record FreezePositionCommand(Long userId, String productCode, Long shares) {
}
