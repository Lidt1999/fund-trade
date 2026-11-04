package com.fund.asset.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PositionRequest(@NotNull @Positive Long userId, @NotBlank String productCode, @NotNull @Positive Long shares) { }
