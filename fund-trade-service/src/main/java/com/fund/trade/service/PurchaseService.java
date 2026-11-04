package com.fund.trade.service;

import com.fund.trade.client.AssetServiceClient;
import com.fund.trade.client.AssetTccCommand;
import com.fund.trade.client.AssetTccFeignClient;
import com.fund.trade.client.PaymentTccClient;
import com.fund.common.api.Result;
import com.fund.trade.entity.PurchaseOrder;
import com.fund.trade.message.OrderRocketMQProducer;
import com.fund.trade.repository.PurchaseOrderRepository;
import com.fund.trade.web.PurchaseRequest;
import com.fund.trade.web.PurchaseResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import io.seata.spring.annotation.GlobalTransactional;

import java.util.UUID;

@Service
public class PurchaseService {
    private final PurchaseOrderRepository orders;
    private final AssetServiceClient assetService;
    private final IdempotencyService idempotency;
    private final PaymentTccClient paymentTcc;
    private final AssetTccFeignClient assetTcc;
    private final OrderRocketMQProducer messageProducer;

    public PurchaseService(PurchaseOrderRepository orders, AssetServiceClient assetService) {
        this(orders, assetService, key -> true, null, null, null);
    }

    public PurchaseService(PurchaseOrderRepository orders, AssetServiceClient assetService,
                           IdempotencyService idempotency) {
        this(orders, assetService, idempotency, null, null, null);
    }

    @Autowired
    public PurchaseService(PurchaseOrderRepository orders, AssetServiceClient assetService,
                           IdempotencyService idempotency, PaymentTccClient paymentTcc,
                           AssetTccFeignClient assetTcc, OrderRocketMQProducer messageProducer) {
        this.orders = orders;
        this.assetService = assetService;
        this.idempotency = idempotency;
        this.paymentTcc = paymentTcc;
        this.assetTcc = assetTcc;
        this.messageProducer = messageProducer;
    }

    @Transactional
    @GlobalTransactional(name = "purchase-tcc", rollbackFor = Exception.class)
    public PurchaseResponse purchase(PurchaseRequest request) {
        PurchaseOrder existing = orders.findByClientRequestId(request.clientRequestId()).orElse(null);
        if (existing != null) {
            return PurchaseResponse.from(existing);
        }

        String key = "trade:idempotency:purchase:" + request.clientRequestId();
        if (!idempotency.tryAcquire(key)) {
            throw new com.fund.common.exception.BusinessException("重复提交申购请求");
        }
        try {
            PurchaseOrder order = new PurchaseOrder(UUID.randomUUID().toString().replace("-", ""),
                    request.clientRequestId(), request.userId(), request.productCode(), request.shares());
            orders.save(order);
            if (paymentTcc != null && assetTcc != null) {
                if (!paymentTcc.tryFreeze(request.clientRequestId(), order.getOrderId(), request.userId(),
                        request.paymentAmount())) {
                    throw new com.fund.common.exception.BusinessException("资金冻结失败");
                }
                Result<Boolean> assetResult = assetTcc.tryReserve(new AssetTccCommand(
                        order.getOrderId(), order.getUserId(), order.getProductCode(), order.getShares(), "TRY"));
                if (!Boolean.TRUE.equals(assetResult.data())) {
                    throw new com.fund.common.exception.BusinessException("资产预留失败");
                }
            } else {
                assetService.createPendingAsset(order.getOrderId(), order.getUserId(), order.getProductCode(), order.getShares());
            }
            if (messageProducer != null) {
                messageProducer.enqueueOrderMessage(order.getOrderId(), order.getUserId(), order.getProductCode(),
                        request.paymentAmount(), 1);
            }
            return PurchaseResponse.from(order);
        } catch (RuntimeException exception) {
            idempotency.release(key);
            throw exception;
        }
    }
}
