package com.fund.trade.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fund.trade.entity.LocalMessage;
import com.fund.trade.repository.LocalMessageRepository;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

@Component
public class OrderRocketMQProducer {
    private static final Logger log = LoggerFactory.getLogger(OrderRocketMQProducer.class);
    private final RocketMQTemplate rocketMQTemplate;
    private final LocalMessageRepository messages;
    private final ObjectMapper objectMapper;
    private final String topic;

    public OrderRocketMQProducer(RocketMQTemplate rocketMQTemplate,
                                 LocalMessageRepository messages,
                                 ObjectMapper objectMapper,
                                 @Value("${rocketmq.topic.order:fund-order-topic}") String topic) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.messages = messages;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    /**
     * Persists the outbox record in the caller's transaction. The scheduler
     * publishes it only after the business transaction has committed.
     */
    public void enqueueOrderMessage(String orderId, Long userId, String productCode,
                                    Long amount, int orderType) {
        OrderMessage payload = new OrderMessage(orderId, userId, productCode, amount,
                orderType, System.currentTimeMillis());
        final String content;
        try {
            content = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("订单消息序列化失败", exception);
        }

        messages.save(new LocalMessage(
                UUID.randomUUID().toString().replace("-", ""), topic, content, 3));
    }

    @Scheduled(fixedDelayString = "${rocketmq.retry.fixed-delay:60000}")
    public void dispatchPendingMessages() {
        List<LocalMessage> pending = messages
                .findTop100ByStatusInAndNextRetryTimeLessThanEqualOrderByCreatedAtAsc(
                        Arrays.asList(LocalMessage.PENDING, LocalMessage.FAILED), LocalDateTime.now());
        for (LocalMessage message : pending) {
            if (message.canRetry()) {
                send(message);
            } else {
                log.error("RocketMQ消息重试次数超限: messageId={}", message.getMessageId());
            }
        }
    }

    public void sendOrderMessage(String orderId, Long userId, String productCode,
                                 Long amount, int orderType) {
        enqueueOrderMessage(orderId, userId, productCode, amount, orderType);
    }

    private void send(LocalMessage message) {
        try {
            rocketMQTemplate.convertAndSend(message.getTopic(), message.getContent());
            message.markSent();
            messages.save(message);
            log.info("RocketMQ消息发送成功: messageId={}", message.getMessageId());
        } catch (RuntimeException exception) {
            message.markFailed();
            messages.save(message);
            log.error("RocketMQ消息发送失败: messageId={}", message.getMessageId(), exception);
        }
    }
}
