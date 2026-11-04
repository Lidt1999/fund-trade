package com.fund.trade.web;

import com.fund.common.api.Result;
import com.fund.trade.entity.ReconciliationError;
import com.fund.trade.entity.ReconciliationReport;
import com.fund.trade.repository.ReconciliationReportRepository;
import com.fund.trade.service.ReconciliationService;
import com.fund.trade.audit.AuditLog;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reconciliations")
public class ReconciliationController {
    private final ReconciliationService service;
    private final ReconciliationReportRepository reports;

    public ReconciliationController(ReconciliationService service,
                                    ReconciliationReportRepository reports) {
        this.service = service;
        this.reports = reports;
    }

    @PostMapping
    @AuditLog(operation = "RECONCILIATION_RUN")
    public Result<ReconciliationReport> reconcile(@RequestParam LocalDate reportDate) {
        return Result.success(service.reconcile(reportDate));
    }

    @GetMapping
    public Result<ReconciliationReport> report(@RequestParam LocalDate reportDate) {
        return Result.success(reports.findByReportDate(reportDate).orElse(null));
    }

    @GetMapping("/errors")
    public Result<List<ReconciliationError>> errors(@RequestParam LocalDate reportDate) {
        return Result.success(service.errors(reportDate));
    }
}
