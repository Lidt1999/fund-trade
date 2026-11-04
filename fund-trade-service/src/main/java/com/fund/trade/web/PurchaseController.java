package com.fund.trade.web;

import com.fund.common.api.Result;
import com.fund.trade.service.PurchaseService;
import com.fund.trade.audit.AuditLog;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) { this.purchaseService = purchaseService; }

    @PostMapping
    @AuditLog(operation = "PURCHASE_SUBMIT")
    public Result<PurchaseResponse> purchase(@Valid @RequestBody PurchaseRequest request) {
        return Result.success(purchaseService.purchase(request));
    }
}
