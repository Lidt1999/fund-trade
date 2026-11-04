package com.fund.payment.web;

import jakarta.validation.constraints.*;

public record PaymentRequest(@NotBlank String requestId, @NotBlank String orderId,
                             @NotNull @Positive Long userId, @NotNull @Positive Long amount) {}
