package com.fund.trade.service;

import com.fund.common.enums.OrderStatusEnum;
import com.fund.trade.entity.*;
import com.fund.trade.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReconciliationService {
    public static final int MATCHED = 1;
    public static final int HAS_ERRORS = 2;
    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

    private final PurchaseOrderRepository purchases;
    private final RedemptionOrderRepository redemptions;
    private final TaConfirmRecordRepository taRecords;
    private final ReconciliationReportRepository reports;
    private final ReconciliationErrorRepository errors;

    public ReconciliationService(PurchaseOrderRepository purchases,
                                 RedemptionOrderRepository redemptions,
                                 TaConfirmRecordRepository taRecords,
                                 ReconciliationReportRepository reports,
                                 ReconciliationErrorRepository errors) {
        this.purchases = purchases;
        this.redemptions = redemptions;
        this.taRecords = taRecords;
        this.reports = reports;
        this.errors = errors;
    }

    @Transactional
    public ReconciliationReport reconcile(LocalDate reportDate) {
        return reports.findByReportDate(reportDate).orElseGet(() -> createReport(reportDate));
    }

    private ReconciliationReport createReport(LocalDate reportDate) {
        LocalDateTime start = reportDate.atStartOfDay();
        LocalDateTime end = reportDate.plusDays(1).atStartOfDay();
        List<TradeOrder> orders = new ArrayList<>();
        purchases.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(start, end)
                .forEach(order -> orders.add(TradeOrder.purchase(order)));
        redemptions.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(start, end)
                .forEach(order -> orders.add(TradeOrder.redemption(order)));

        List<TaConfirmRecord> confirmations = taRecords.findByConfirmDate(reportDate);
        Map<String, TaConfirmRecord> taByOrder = confirmations.stream()
                .collect(Collectors.toMap(TaConfirmRecord::getOrderId, Function.identity()));
        Map<String, TradeOrder> systemByOrder = orders.stream()
                .collect(Collectors.toMap(TradeOrder::orderId, Function.identity()));

        ReconciliationReport report = new ReconciliationReport(reportDate);
        report.setTotalOrders(orders.size());
        int matched = 0;
        int mismatch = 0;
        int missingInTa = 0;
        int missingInSystem = 0;
        List<ReconciliationError> reportErrors = new ArrayList<>();

        for (TradeOrder order : orders) {
            TaConfirmRecord ta = taByOrder.get(order.orderId());
            if (ta == null) {
                missingInTa++;
                reportErrors.add(error(null, order, "TA无记录"));
            } else if (order.shares().equals(ta.getConfirmedAmount())
                    && statusMatches(order.status(), ta.getConfirmStatus())) {
                matched++;
            } else {
                mismatch++;
                reportErrors.add(error(ta, order, "不一致"));
            }
        }
        for (TaConfirmRecord ta : confirmations) {
            if (!systemByOrder.containsKey(ta.getOrderId())) {
                missingInSystem++;
                reportErrors.add(error(ta, null, "系统无记录"));
            }
        }

        report.setMatchedCount(matched);
        report.setMismatchCount(mismatch);
        report.setMissingInTa(missingInTa);
        report.setMissingInSystem(missingInSystem);
        report.setStatus(reportErrors.isEmpty() ? MATCHED : HAS_ERRORS);
        ReconciliationReport saved = reports.save(report);
        reportErrors.forEach(error -> errors.save(new ReconciliationError(saved.getId(),
                error.getErrorType(), error.getOrderId(), error.getSystemAmount(),
                error.getSystemStatus(), error.getTaAmount(), error.getTaStatus())));
        if (reportErrors.isEmpty()) {
            log.info("日终对账完成: date={}, matched={}", reportDate, matched);
        } else {
            log.warn("日终对账发现差异: date={}, errors={}", reportDate, reportErrors.size());
        }
        return saved;
    }

    public List<ReconciliationError> errors(LocalDate reportDate) {
        return reports.findByReportDate(reportDate)
                .map(report -> errors.findByReportId(report.getId()))
                .orElseGet(List::of);
    }

    private boolean statusMatches(String status, int taStatus) {
        return (taStatus == TaConfirmRecord.SUCCESS
                && (OrderStatusEnum.CONFIRMED.getCode().equals(status)
                || OrderStatusEnum.REDEEMED.getCode().equals(status)))
                || (taStatus == TaConfirmRecord.FAILED
                && OrderStatusEnum.CONFIRM_FAILED.getCode().equals(status));
    }

    private ReconciliationError error(TaConfirmRecord ta, TradeOrder order, String type) {
        return new ReconciliationError(null, type,
                order == null ? ta.getOrderId() : order.orderId(),
                order == null ? null : order.shares(),
                order == null ? null : order.status(),
                ta == null ? null : ta.getConfirmedAmount(),
                ta == null ? null : ta.getConfirmStatus());
    }

    private record TradeOrder(String orderId, Long shares, String status) {
        static TradeOrder purchase(PurchaseOrder order) {
            return new TradeOrder(order.getOrderId(), order.getShares(), order.getStatus());
        }
        static TradeOrder redemption(RedemptionOrder order) {
            return new TradeOrder(order.getOrderId(), order.getShares(), order.getStatus());
        }
    }
}
