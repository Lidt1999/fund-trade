package com.fund.trade.client;

import com.fund.common.api.Result;
import org.springframework.stereotype.Component;

@Component
public class PaymentTccClient {
    private final PaymentTccFeignClient client;

    public PaymentTccClient(PaymentTccFeignClient client) {
        this.client = client;
    }

    public boolean tryFreeze(String requestId, String orderId, Long userId, Long amount) {
        Result<Boolean> result = client.tryFreeze(new PaymentTccCommand(requestId, orderId, userId, amount));
        return Boolean.TRUE.equals(result.data());
    }
}
