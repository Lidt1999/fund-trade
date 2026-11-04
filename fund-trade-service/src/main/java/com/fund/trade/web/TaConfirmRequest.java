package com.fund.trade.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record TaConfirmRequest(
        @NotBlank String fileId,
        @NotBlank String orderId,
        @NotNull Integer confirmStatus,
        @NotNull @Positive Long confirmedAmount,
        String failReason,
        @NotNull LocalDate confirmDate) { }
