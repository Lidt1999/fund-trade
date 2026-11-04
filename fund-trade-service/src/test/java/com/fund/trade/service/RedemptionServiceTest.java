package com.fund.trade.service;

import com.fund.trade.client.AssetServiceClient;
import com.fund.trade.entity.RedemptionOrder;
import com.fund.trade.repository.RedemptionOrderRepository;
import com.fund.trade.web.RedemptionRequest;
import com.fund.trade.web.RedemptionResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedemptionServiceTest {
    private final RedemptionOrderRepository orders = mock(RedemptionOrderRepository.class);
    private final AssetServiceClient assetService = mock(AssetServiceClient.class);
    private final RedemptionService service = new RedemptionService(orders, assetService);

    @Test
    void freezesSharesAndCreatesOrder() {
        RedemptionRequest request = new RedemptionRequest("redeem-001", 1001L, "FUND001", 200L);
        when(orders.findByClientRequestId(request.clientRequestId())).thenReturn(Optional.empty());
        when(orders.save(any(RedemptionOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RedemptionResponse response = service.redeem(request);

        assertThat(response.orderId()).hasSize(32);
        assertThat(response.status()).isEqualTo("PENDING_CONFIRMATION");
        verify(assetService).freezePositionShares(1001L, "FUND001", 200L);
    }

    @Test
    void returnsExistingOrderWithoutFreezingAgain() {
        RedemptionOrder existing = new RedemptionOrder("order-001", "redeem-001", 1001L, "FUND001", 200L);
        when(orders.findByClientRequestId("redeem-001")).thenReturn(Optional.of(existing));

        RedemptionResponse response = service.redeem(new RedemptionRequest("redeem-001", 1001L, "FUND001", 200L));

        assertThat(response.orderId()).isEqualTo("order-001");
        verify(orders, never()).save(any());
        verify(assetService, never()).freezePositionShares(any(), any(), any());
    }
}
