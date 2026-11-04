package com.fund.trade.service;

import com.fund.common.enums.OrderStatusEnum;
import com.fund.trade.client.AssetConfirmationFeignClient;
import com.fund.trade.entity.PurchaseOrder;
import com.fund.trade.entity.RedemptionOrder;
import com.fund.trade.entity.TaConfirmRecord;
import com.fund.trade.repository.PurchaseOrderRepository;
import com.fund.trade.repository.RedemptionOrderRepository;
import com.fund.trade.repository.TaConfirmRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TADailyConfirmService {
    private static final Logger log = LoggerFactory.getLogger(TADailyConfirmService.class);
    private final PurchaseOrderRepository purchases;
    private final RedemptionOrderRepository redemptions;
    private final TaConfirmRecordRepository records;
    private final AssetConfirmationFeignClient assets;
    private final OrderStateMachine stateMachine;
    private final BusinessDayCalendar calendar;

    public TADailyConfirmService(PurchaseOrderRepository purchases,
                                 RedemptionOrderRepository redemptions,
                                 TaConfirmRecordRepository records,
                                 AssetConfirmationFeignClient assets,
                                 OrderStateMachine stateMachine,
                                 BusinessDayCalendar calendar) {
        this.purchases = purchases;
        this.redemptions = redemptions;
        this.records = records;
        this.assets = assets;
        this.stateMachine = stateMachine;
        this.calendar = calendar;
    }

    @Transactional
    public int processDailyConfirm(LocalDate confirmDate) {
        if (!calendar.isBusinessDay(confirmDate)) {
            log.info("非工作日，跳过TA确认: confirmDate={}", confirmDate);
            return 0;
        }
        LocalDate tradeDate = calendar.previousBusinessDay(confirmDate);
        int processed = 0;
        for (PurchaseOrder order : purchases.findByStatus(OrderStatusEnum.PENDING_CONFIRMATION.getCode())) {
            if (order.getCreatedAt() != null && !order.getCreatedAt().toLocalDate().equals(tradeDate)) {
                continue;
            }
            processPurchase(order, confirmDate);
            processed++;
        }
        for (RedemptionOrder order : redemptions.findByStatus(OrderStatusEnum.PENDING_CONFIRMATION.getCode())) {
            if (order.getCreatedAt() != null && !order.getCreatedAt().toLocalDate().equals(tradeDate)) {
                continue;
            }
            processRedemption(order, confirmDate);
            processed++;
        }
        log.info("TA模拟确认完成: tradeDate={}, confirmDate={}, processed={}",
                tradeDate, confirmDate, processed);
        return processed;
    }

    private void processPurchase(PurchaseOrder order, LocalDate confirmDate) {
        TaConfirmRecord record = confirmationFor(order.getOrderId(), order.getShares(), confirmDate);
        if (record.getConfirmStatus() == TaConfirmRecord.SUCCESS) {
            assets.confirmPending(order.getOrderId());
            stateMachine.transition(order, OrderStatusEnum.CONFIRMED);
        } else {
            assets.cancelPending(order.getOrderId());
            stateMachine.transition(order, OrderStatusEnum.CONFIRM_FAILED);
        }
        purchases.save(order);
    }

    private void processRedemption(RedemptionOrder order, LocalDate confirmDate) {
        TaConfirmRecord record = confirmationFor(order.getOrderId(), order.getShares(), confirmDate);
        stateMachine.transition(order, record.getConfirmStatus() == TaConfirmRecord.SUCCESS
                ? OrderStatusEnum.CONFIRMED : OrderStatusEnum.CONFIRM_FAILED);
        redemptions.save(order);
    }

    private TaConfirmRecord confirmationFor(String orderId, Long amount, LocalDate confirmDate) {
        return records.findByOrderId(orderId).orElseGet(() -> records.save(new TaConfirmRecord(
                "SIM-" + UUID.randomUUID().toString().replace("-", ""),
                orderId, TaConfirmRecord.SUCCESS, amount, null, confirmDate)));
    }

    public List<TaConfirmRecord> confirmations(LocalDate confirmDate) {
        return records.findByConfirmDate(confirmDate);
    }
}
