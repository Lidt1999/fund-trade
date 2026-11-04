package com.fund.payment.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AccountRequest(@NotNull Long userId, @NotNull @PositiveOrZero Long availableBalance) {}
