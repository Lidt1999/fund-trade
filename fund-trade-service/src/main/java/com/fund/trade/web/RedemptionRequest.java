package com.fund.trade.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RedemptionRequest(
        @NotBlank String clientRequestId,
        @NotNull @Positive Long userId,
        @NotBlank String productCode,
        @NotNull @Positive Long shares) { }
