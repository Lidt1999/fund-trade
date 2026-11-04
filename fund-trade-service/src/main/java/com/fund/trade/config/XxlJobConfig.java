package com.fund.trade.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XxlJobConfig {
    @Bean(initMethod = "start", destroyMethod = "destroy")
    @ConditionalOnProperty(name = "xxl.job.enabled", havingValue = "true")
    public XxlJobSpringExecutor xxlJobExecutor(
            @Value("${xxl.job.admin.addresses}") String adminAddresses,
            @Value("${xxl.job.executor.appname:fund-trade-service}") String appName,
            @Value("${xxl.job.executor.address:}") String address,
            @Value("${xxl.job.executor.ip:}") String ip,
            @Value("${xxl.job.executor.port:9999}") int port,
            @Value("${xxl.job.access-token:default_token}") String accessToken,
            @Value("${xxl.job.executor.log-path:./logs/xxl-job}") String logPath,
            @Value("${xxl.job.executor.log-retention-days:30}") int logRetentionDays) {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appName);
        executor.setAddress(address);
        executor.setIp(ip);
        executor.setPort(port);
        executor.setAccessToken(accessToken);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
        return executor;
    }
}
