package com.fund.trade.config;

import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.fund.trade.job.TAConfirmJob;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TradeSentinelConfig {
    @PostConstruct
    public void loadTaConfirmationCircuitBreaker() {
        DegradeRule rule = new DegradeRule(TAConfirmJob.RESOURCE)
                .setGrade(CircuitBreakerStrategy.ERROR_COUNT.getType())
                .setCount(1)
                .setMinRequestAmount(1)
                .setStatIntervalMs(10_000)
                .setTimeWindow(30);
        DegradeRuleManager.loadRules(List.of(rule));
    }
}
