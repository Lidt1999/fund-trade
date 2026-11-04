package com.fund.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SentinelConfig {
    private static final String PURCHASE_API = "purchase_api";
    private static final String REDEMPTION_API = "redemption_api";

    @PostConstruct
    public void initGatewayRules() {
        Set<ApiDefinition> apiDefinitions = new HashSet<>();
        apiDefinitions.add(apiDefinition(PURCHASE_API, "/api/trades/purchases"));
        apiDefinitions.add(apiDefinition(REDEMPTION_API, "/api/trades/redemptions"));
        GatewayApiDefinitionManager.loadApiDefinitions(apiDefinitions);

        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule(PURCHASE_API).setCount(1000).setIntervalSec(1));
        rules.add(new GatewayFlowRule(REDEMPTION_API).setCount(800).setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }

    private ApiDefinition apiDefinition(String name, String path) {
        Set<ApiPredicateItem> predicates = new HashSet<>();
        predicates.add(new ApiPathPredicateItem().setPattern(path));
        return new ApiDefinition(name).setPredicateItems(predicates);
    }
}
