package com.fund.trade.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "${rocketmq.topic.order:fund-order-topic}",
        consumerGroup = "${rocketmq.consumer.group:fund-trade-consumer}")
public class TAOrderMessageConsumer implements RocketMQListener<String> {
    private static final Logger log = LoggerFactory.getLogger(TAOrderMessageConsumer.class);
    private final ObjectMapper objectMapper;

    public TAOrderMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(String message) {
        try {
            OrderMessage order = objectMapper.readValue(message, OrderMessage.class);
            log.info("收到TA订单通知: orderId={}, userId={}, productCode={}, orderType={}",
                    order.orderId(), order.userId(), order.productCode(), order.orderType());
        } catch (JsonProcessingException exception) {
            log.error("RocketMQ订单消息解析失败: {}", message, exception);
            throw new IllegalArgumentException("订单消息格式非法", exception);
        }
    }
}
