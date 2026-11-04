package com.fund.trade.service;

import com.fund.trade.client.AssetServiceClient;
import com.fund.trade.entity.PurchaseOrder;
import com.fund.trade.repository.PurchaseOrderRepository;
import com.fund.trade.web.PurchaseRequest;
import com.fund.trade.web.PurchaseResponse;
import com.fund.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PurchaseServiceTest {
    private final PurchaseOrderRepository orders = mock(PurchaseOrderRepository.class);
    private final AssetServiceClient assetService = mock(AssetServiceClient.class);
    private final PurchaseService service = new PurchaseService(orders, assetService);

    @Test
    void createsOrderAndPendingAsset() {
        PurchaseRequest request = new PurchaseRequest("request-001", 1001L, "FUND001", 200L);
        when(orders.findByClientRequestId(request.clientRequestId())).thenReturn(Optional.empty());
        when(orders.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseResponse response = service.purchase(request);

        assertThat(response.orderId()).hasSize(32);
        assertThat(response.status()).isEqualTo("PENDING_CONFIRMATION");
        verify(assetService).createPendingAsset(response.orderId(), 1001L, "FUND001", 200L);
    }

    @Test
    void returnsExistingOrderForRepeatedRequest() {
        PurchaseOrder existing = new PurchaseOrder("order-001", "request-001", 1001L, "FUND001", 200L);
        when(orders.findByClientRequestId("request-001")).thenReturn(Optional.of(existing));

        PurchaseResponse response = service.purchase(new PurchaseRequest("request-001", 1001L, "FUND001", 200L));

        assertThat(response.orderId()).isEqualTo("order-001");
        verify(orders, never()).save(any());
        verify(assetService, never()).createPendingAsset(any(), any(), any(), any());
    }

    @Test
    void rejectsRequestWhenRedisKeyIsAlreadyHeld() {
        IdempotencyService idempotency = mock(IdempotencyService.class);
        when(idempotency.tryAcquire("trade:idempotency:purchase:request-001")).thenReturn(false);
        PurchaseService guardedService = new PurchaseService(orders, assetService, idempotency);
        when(orders.findByClientRequestId("request-001")).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        guardedService.purchase(new PurchaseRequest("request-001", 1001L, "FUND001", 200L)))
                .isInstanceOf(BusinessException.class);
        verify(assetService, never()).createPendingAsset(any(), any(), any(), any());
    }
}
