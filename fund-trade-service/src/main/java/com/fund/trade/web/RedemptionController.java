package com.fund.trade.web;

import com.fund.common.api.Result;
import com.fund.trade.service.RedemptionService;
import com.fund.trade.audit.AuditLog;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades/redemptions")
public class RedemptionController {
    private final RedemptionService redemptionService;

    public RedemptionController(RedemptionService redemptionService) { this.redemptionService = redemptionService; }

    @PostMapping
    @AuditLog(operation = "REDEMPTION_SUBMIT")
    public Result<RedemptionResponse> redeem(@Valid @RequestBody RedemptionRequest request) {
        return Result.success(redemptionService.redeem(request));
    }
}
