/**
 * @(#)AbstractRabbitmqConsumerConfiguration.java, 一月 22, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.mq.rabbitmq;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.infrastructure.mq.ConsumeStatauEnum;
import com.memberclub.infrastructure.mq.MQQueueEnum;
import com.memberclub.infrastructure.mq.MessageQueueConsumerFacade;
import com.memberclub.infrastructure.mq.RabbitRegisterConfiguration;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ 消费者的基础配置类，启动时会发现所有 {@link MessageQueueConsumerFacade} Bean，
 * 并将消息处理委托给它们，同时提供确认和简单重试的辅助方法。
 *
 * <p>具体的消费者配置类需继承此类，并将队列绑定到
 * {@link #consume(String, Channel, Message, MQQueueEnum)} 或
 * {@link #consumeAndFailRetry(String, Channel, Message, SwitchEnum, MQQueueEnum)} 方法。</p>
 *
 * author: 掘金五阳
 */
public class AbstractRabbitmqConsumerConfiguration {

    public static final Logger LOG = LoggerFactory.getLogger(RabbitmqConsumerConfiguration.class);
    public static final String RETRY_COUNT = "retry-count";

    private Map<String, List<MessageQueueConsumerFacade>> consumerMap = Maps.newHashMap();

    /**
     * 启动时扫描所有 {@link MessageQueueConsumerFacade} Bean，并按队列名称分组，
     * 便于消息消费时快速定位。
     */
    @PostConstruct
    public void init() {
        Map<String, MessageQueueConsumerFacade> consumers = null;
        try {
            consumers =
                    ApplicationContextUtils.getContext().getBeansOfType(MessageQueueConsumerFacade.class);
        } catch (Exception e) {
            // 如果没有定义消费者，忽略异常继续
        }
        if (MapUtils.isNotEmpty(consumers)) {
            for (Map.Entry<String, MessageQueueConsumerFacade> entry : consumers.entrySet()) {
                MQQueueEnum mqQueueEnum = entry.getValue().register();
                consumerMap.putIfAbsent(mqQueueEnum.getQueneName(), Lists.newArrayList());
                consumerMap.get(mqQueueEnum.getQueneName()).add(entry.getValue());
            }
        }
    }


    /**
     * 处理消息，只有所有消费者都成功时才确认；若有消费者要求重试或抛出异常，则不确认并抛出异常。
     *
     * @param value   消息内容
     * @param channel RabbitMQ 渠道，用于确认消息
     * @param message 原始 Spring AMQP 消息
     * @param queue   队列元信息
     * @throws IOException 确认失败时抛出
     */
    protected void consume(String value, Channel channel, Message message,
                           MQQueueEnum queue) throws IOException {
        LOG.info("收到rabbitmq消息 queue:{}, message:{}", queue.getDelayQueneName(), value);

        boolean fail = false;
        RuntimeException exception = null;
        for (MessageQueueConsumerFacade messageQueueConsumerFacade : consumerMap.get(queue.getQueneName())) {
            try {
                ConsumeStatauEnum status = messageQueueConsumerFacade.consume(value);
                if (status == ConsumeStatauEnum.retry) {
                    // 消费者要求重试，不立即确认
                    fail = true;
                }
            } catch (RuntimeException e) {
                exception = e;
                fail = true;
            }
        }
        if (!fail) {
            // 所有消费者成功处理后确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        // 如处理失败，将最后的异常向上抛出
        throw exception;
    }

    /**
     * 处理消息，失败时通过延迟队列进行重试，重试次数记录在消息头并受配置项限制。
     *
     * @param value       消息内容
     * @param channel     RabbitMQ 渠道，用于确认消息
     * @param message     原始 Spring AMQP 消息
     * @param retryConfig 最大重试次数的配置项
     * @param queue       队列元信息
     * @throws IOException 确认或重新投递失败时抛出
     */
    protected void consumeAndFailRetry(String value, Channel channel, Message message,
                                       SwitchEnum retryConfig,
                                       MQQueueEnum queue) throws IOException {
        LOG.info("收到rabbitmq消息 queue:{}, message:{}", queue.getDelayQueneName(), value);

        boolean fail = false;
        for (MessageQueueConsumerFacade messageQueueConsumerFacade : consumerMap.get(queue.getQueneName())) {
            try {
                ConsumeStatauEnum status = messageQueueConsumerFacade.consume(value);
                if (status == ConsumeStatauEnum.retry) {
                    fail = true;
                }
            } catch (Exception e) {
                // 任何异常都会进入重试流程
                fail = true;
            }
        }
        if (!fail) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        Integer retryCount;
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        if (!headers.containsKey(RETRY_COUNT)) {
            retryCount = 0;
        } else {
            retryCount = (Integer) headers.get(RETRY_COUNT);
        }
        // 判断是否满足最大重试次数
        int maxRetryTimes = retryConfig.getInt();
        if (retryCount++ < maxRetryTimes) {
            headers.put("retry-count", retryCount);
            // 发送到延迟队列等待重试
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                    .builder().contentType("text/plain").headers(headers).build();
            channel.basicPublish(RabbitRegisterConfiguration.DEAD_LETTER_EXCHANGE,
                    queue.getDelayQueneName(), basicProperties,
                    message.getBody());
            LOG.warn("消费失败,将消息延迟 {} 毫秒后,重新投递到队列:{}, 当前:retryCount:{}, maxRetryCount:{}, message:{}",
                    queue.getDelayMillSeconds(),
                    queue.getQueneName(),
                    (retryCount - 1),
                    maxRetryTimes, value);
        } else {
            // 达到最大重试次数后确认并丢弃消息
            LOG.warn("消费失败,达到最大重试次数,无法重新投递到队列:{}, 当前:retryCount:{}, maxRetryCount:{}, message:{}",
                    queue.getQueneName(),
                    (retryCount - 1),
                    maxRetryTimes, value);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}