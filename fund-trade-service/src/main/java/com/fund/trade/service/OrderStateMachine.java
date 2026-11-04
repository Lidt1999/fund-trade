package com.fund.trade.service;

import com.fund.common.enums.OrderStatusEnum;
import com.fund.common.exception.BusinessException;
import com.fund.trade.entity.PurchaseOrder;
import com.fund.trade.entity.RedemptionOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Centralizes order transitions so callers cannot skip a business state.
 */
@Service
public class OrderStateMachine {
    private static final Logger log = LoggerFactory.getLogger(OrderStateMachine.class);

    public void transition(PurchaseOrder order, OrderStatusEnum targetStatus) {
        transition(order.getOrderId(), order.getStatus(), targetStatus, order::setStatus);
    }

    public void transition(RedemptionOrder order, OrderStatusEnum targetStatus) {
        transition(order.getOrderId(), order.getStatus(), targetStatus, order::setStatus);
    }

    private void transition(String orderId, String currentCode, OrderStatusEnum targetStatus,
                            java.util.function.Consumer<String> statusWriter) {
        if (targetStatus == null) {
            throw new BusinessException("目标状态不能为空");
        }

        OrderStatusEnum currentStatus = OrderStatusEnum.fromCode(currentCode);
        if (currentStatus == null) {
            throw new BusinessException("未知订单状态: " + currentCode);
        }
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new BusinessException("状态转换非法: " + currentStatus.getCode()
                    + " -> " + targetStatus.getCode());
        }

        statusWriter.accept(targetStatus.getCode());
        log.info("Order status transitioned: orderId={}, {} -> {}",
                orderId, currentStatus.getCode(), targetStatus.getCode());
    }
}
