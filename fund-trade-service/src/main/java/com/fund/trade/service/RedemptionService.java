package com.fund.trade.service;

import com.fund.trade.client.AssetServiceClient;
import com.fund.trade.entity.RedemptionOrder;
import com.fund.trade.message.OrderRocketMQProducer;
import com.fund.trade.repository.RedemptionOrderRepository;
import com.fund.trade.web.RedemptionRequest;
import com.fund.trade.web.RedemptionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Service
public class RedemptionService {
    private final RedemptionOrderRepository orders;
    private final AssetServiceClient assetService;
    private final IdempotencyService idempotency;
    private final OrderRocketMQProducer messageProducer;

    public RedemptionService(RedemptionOrderRepository orders, AssetServiceClient assetService) {
        this(orders, assetService, key -> true, null);
    }

    public RedemptionService(RedemptionOrderRepository orders, AssetServiceClient assetService,
                             IdempotencyService idempotency) {
        this(orders, assetService, idempotency, null);
    }

    @Autowired
    public RedemptionService(RedemptionOrderRepository orders, AssetServiceClient assetService,
                            IdempotencyService idempotency, OrderRocketMQProducer messageProducer) {
        this.orders = orders;
        this.assetService = assetService;
        this.idempotency = idempotency;
        this.messageProducer = messageProducer;
    }

    @Transactional
    public RedemptionResponse redeem(RedemptionRequest request) {
        RedemptionOrder existing = orders.findByClientRequestId(request.clientRequestId()).orElse(null);
        if (existing != null) return RedemptionResponse.from(existing);

        String key = "trade:idempotency:redemption:" + request.clientRequestId();
        if (!idempotency.tryAcquire(key)) {
            throw new com.fund.common.exception.BusinessException("重复提交赎回请求");
        }
        try {
            assetService.freezePositionShares(request.userId(), request.productCode(), request.shares());
            RedemptionOrder order = new RedemptionOrder(UUID.randomUUID().toString().replace("-", ""),
                    request.clientRequestId(), request.userId(), request.productCode(), request.shares());
            orders.save(order);
            if (messageProducer != null) {
                messageProducer.enqueueOrderMessage(order.getOrderId(), order.getUserId(), order.getProductCode(),
                        order.getShares(), 2);
            }
            return RedemptionResponse.from(order);
        } catch (RuntimeException exception) {
            idempotency.release(key);
            throw exception;
        }
    }
}
