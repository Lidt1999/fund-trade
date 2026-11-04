package com.fund.trade;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@Import(RocketMQAutoConfiguration.class)
public class FundTradeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FundTradeServiceApplication.class, args);
    }
}
