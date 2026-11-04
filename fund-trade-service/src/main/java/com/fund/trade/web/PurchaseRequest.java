package com.fund.trade.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotBlank String clientRequestId,
        @NotNull @Positive Long userId,
        @NotBlank String productCode,
        @NotNull @Positive Long shares,
        @Positive Long amount) {
    public PurchaseRequest(String clientRequestId, Long userId, String productCode, Long shares) {
        this(clientRequestId, userId, productCode, shares, null);
    }

    public long paymentAmount() {
        return amount == null ? shares : amount;
    }
}
