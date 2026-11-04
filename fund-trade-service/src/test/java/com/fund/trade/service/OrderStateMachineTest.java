package com.fund.trade.service;

import com.fund.common.enums.OrderStatusEnum;
import com.fund.common.exception.BusinessException;
import com.fund.trade.entity.PurchaseOrder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderStateMachineTest {
    private final OrderStateMachine stateMachine = new OrderStateMachine();

    @Test
    void allowsConfirmationSuccess() {
        PurchaseOrder order = new PurchaseOrder("order-001", "request-001", 1001L, "FUND001", 200L);

        stateMachine.transition(order, OrderStatusEnum.CONFIRMED);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.CONFIRMED.getCode());
    }

    @Test
    void rejectsSkippingConfirmation() {
        PurchaseOrder order = new PurchaseOrder("order-001", "request-001", 1001L, "FUND001", 200L);

        assertThatThrownBy(() -> stateMachine.transition(order, OrderStatusEnum.REDEEMED))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("状态转换非法");
    }

    @Test
    void rejectsTransitionFromTerminalState() {
        PurchaseOrder order = new PurchaseOrder("order-001", "request-001", 1001L, "FUND001", 200L);
        order.setStatus(OrderStatusEnum.CONFIRM_FAILED.getCode());

        assertThatThrownBy(() -> stateMachine.transition(order, OrderStatusEnum.CONFIRMED))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectsUnknownCurrentState() {
        PurchaseOrder order = new PurchaseOrder("order-001", "request-001", 1001L, "FUND001", 200L);
        order.setStatus("UNKNOWN");

        assertThatThrownBy(() -> stateMachine.transition(order, OrderStatusEnum.CONFIRMED))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("未知订单状态");
    }
}
