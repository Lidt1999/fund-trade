package com.fund.trade.client;

import com.fund.common.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.base-url}")
public interface PaymentTccFeignClient {
    @PostMapping("/api/payments/tcc/try")
    Result<Boolean> tryFreeze(@RequestBody PaymentTccCommand command);
}
