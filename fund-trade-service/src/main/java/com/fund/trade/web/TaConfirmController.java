package com.fund.trade.web;

import com.fund.common.api.Result;
import com.fund.trade.entity.TaConfirmRecord;
import com.fund.trade.audit.AuditLog;
import com.fund.trade.repository.TaConfirmRecordRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ta/confirmations")
public class TaConfirmController {
    private final TaConfirmRecordRepository records;

    public TaConfirmController(TaConfirmRecordRepository records) {
        this.records = records;
    }

    @GetMapping
    public Result<List<TaConfirmRecord>> list(@RequestParam LocalDate confirmDate) {
        return Result.success(records.findByConfirmDate(confirmDate));
    }

    @PostMapping
    @AuditLog(operation = "TA_CONFIRM_IMPORT")
    public Result<TaConfirmRecord> create(@Valid @RequestBody TaConfirmRequest request) {
        TaConfirmRecord record = records.findByOrderId(request.orderId()).orElse(null);
        if (record != null) {
            return Result.success(record);
        }
        return Result.success(records.save(new TaConfirmRecord(request.fileId(), request.orderId(),
                request.confirmStatus(), request.confirmedAmount(), request.failReason(), request.confirmDate())));
    }
}
